#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

int main(void) {
    const size_t n = 1000000000;
    double *data = malloc(n * sizeof *data);
    if (!data) { return 1; }
    double start = omp_get_wtime();

    // LOOP 1: create synthetic data
    for (size_t i = 0; i < n; ++i) { data[i] = i % 10; }

    // LOOP 2: compute sum
    double sum = 0.0;
    for (size_t i = 0; i < n; ++i) { sum += data[n - 1 - i]; }

    // LOOP 3: compute sum of squares
    double sum_sq = 0.0;
    for (size_t i = 0; i < n; ++i) { sum_sq += data[i] * data[i]; }

    double end = omp_get_wtime();
    double mean = sum / (double)n;
    double mean_sq = sum_sq / (double)n;
    double variance = mean_sq - mean * mean;

    printf("variance       = %.2f\n", variance);
    printf("total time (s) = %.2f\n", end - start);
    printf("num threads    = %d\n\n", omp_get_max_threads());

    free(data);
    return 0;
}