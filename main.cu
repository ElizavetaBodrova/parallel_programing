#include <stdio.h>
#include <iostream>
#include <algorithm>
#include <cuda_runtime.h>
#include <random>
#include <chrono>

__device__ bool isPrime(const int number) {
    int divider = 2;
    bool flag = true;
    while (divider < number - 1 && flag) {
        flag = number % divider != 0;
        divider++;
    }
    return flag;
}


bool isPrimeNonParallel(const int number) {
    int divider = 2;
    bool flag = true;
    while (divider < number - 1 && flag) {
        flag = number % divider != 0;
        divider++;
    }
    return flag;
}

int findCountNonParallel(int *arr, int N) {
    int localCount = 0;
    for (int i = 0; i < N * N; i++) {
        if (isPrimeNonParallel(arr[i])) {
            localCount++;
        }
    }
    return localCount;
}

//каждый поток
__global__ void _primeCount(int *matrix, const int N, int *result) {
    //рассчитывание координат

    int globalIdx = blockIdx.x * blockDim.x + threadIdx.x;

    while (globalIdx < N) {
        if (isPrime(matrix[globalIdx])) {
            atomicAdd(&(result[0]), 1);
            matrix[globalIdx] = 0;
        }
        globalIdx += blockDim.x * gridDim.x;
        __syncthreads();

    }

}

void initMatrix(int *matrix, const int N) {
        srand(time(NULL));
        for (int i = 0; i < N * N; ++i) {
            matrix[i] = 1 + rand() % 10;
          //  std::cout << matrix[i] << " ";
        }
      //  std::cout << std::endl;
    }

int main() {
    //const int N = 1 << 10; // 1024
    const int N = 8; // 24
    std::cout << "N = " << N << std::endl;
    size_t bytes = (N * N) * sizeof(N);
    int *matrix;
    cudaMallocManaged(&matrix, bytes);
    initMatrix(matrix, N);

    std::cout << "NonParallel " << findCountNonParallel(matrix, N * N) % (N * N) << std::endl;

// Copy data to device
    int globalResult = 0;
    int* d_test_data, * global;
    cudaMalloc(&global, sizeof(int));
    cudaMalloc(&d_test_data, N * sizeof(int));
    cudaMemcpy(global, &globalResult, sizeof(int), cudaMemcpyHostToDevice);
    cudaMemcpy(d_test_data, matrix, N*N * sizeof(int), cudaMemcpyHostToDevice);

// Launch kernel
    _primeCount << <10, 1024 >> > (matrix,N*N, global);

// Copy results back to device
    cudaDeviceSynchronize();
    cudaMemcpy(matrix, d_test_data, N*N * sizeof(int), cudaMemcpyDeviceToHost);
    cudaMemcpy(&globalResult, global, sizeof(int), cudaMemcpyDeviceToHost);
    cudaFree(d_test_data);
    cudaFree(global);

    std::cout << "Work is done! " << globalResult << std::endl;
    for (int i = 0; i <N*N; ++i) {
        std::cout << matrix[i] << " ";
    }
    std::cout << std::endl;
    system("pause");
    return 0;
}
