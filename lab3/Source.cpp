#include <omp.h>
#include <iostream>
#include <algorithm>
#include <thread>

using namespace std;

const int MAX_THREADS = 4;
const int COUNT = 256*256;


bool isPrime(int& value)
{
	int divider = 2;
	bool flag = true;
	while (divider < value - 1 && flag) {
		flag = value % divider != 0;
		divider++;
	}
	return flag;
}

void findCount(int* array, int start, int end, int& count)
{

	int localCount = 0;
	for (int i = start; i < end; i++) {
		if (isPrime(array[i])) {
			localCount++;
		}
	}
	count += localCount;
}

void NonParallelTask( int* arr)
{
	int localCount = 0;

	for (int i = 0; i < COUNT; i++) {
		if (isPrime(arr[i])) {
			localCount++;
		}
	}

	printf("Found %d prime numbers\n", localCount);
}

void ParallelTask(int*arr)
{
	const int minBlock = 25;//мин размер блока
	const int maxThreads = (COUNT + minBlock - 1) / minBlock;
	const int hard_thread = std::thread::hardware_concurrency();//системное число потоков
	const int count_threads = min((hard_thread != 0 ? hard_thread : MAX_THREADS), maxThreads);
	const int blockSize = COUNT / count_threads;
//	printf(" %d ,%d ,%d ,%d \n", maxThreads,hard_thread,count_threads,blockSize);
	int i=0,count;
	int globalCount = 0;

#pragma omp parallel num_threads(count_threads) private(i,count) shared( globalCount,arr)
	{
		count = 0;
		i = omp_get_thread_num();
			findCount(arr, blockSize * i, blockSize * (i + 1), count);
			

#pragma omp atomic
			globalCount += count;
			//cout << "i" << i << " " << count << std::endl;
		
	}

	printf("Found %d prime numbers.\n", globalCount);
}
void init(int* arr) {
	srand(time(NULL));
	for (int i = 0; i < COUNT; i++)
		arr[i] = 1 + rand() % 100000;//1000
}
void print(int* arr) {
	for (int i = 0; i < COUNT; i++)
		std::cout << arr[i] << " ";
	std::cout << std::endl;
}

int main()
{
	int arr[COUNT];
	init(arr);
	//print(arr);
	double start, end, middle;

	start = omp_get_wtime();
	NonParallelTask(arr);
	middle = omp_get_wtime();
	/*ParallelTask(arr);
	end = omp_get_wtime();*/
	printf("Work took %f seconds (nonparallel)\nWork took  seconds (parallel)\n", middle - start);
}