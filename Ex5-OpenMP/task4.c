#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

int main(void) {

    const size_t n = 100000000;   // 1e8 elements (fast enough but measurable)
    double *data = malloc(n * sizeof *data);
    if (!data) { return 1; }

    double start, end, sum, sum_sq;


    /* ============================================================
       Version 1: Sequential (Listing 2)
       ============================================================ */
    printf("===== Sequential =====\n");

    start = omp_get_wtime();

    // LOOP 1
    for (size_t i = 0; i < n; ++i)
        data[i] = i % 10;

    // LOOP 2
    sum = 0.0;
    for (size_t i = 0; i < n; ++i)
        sum += data[n - 1 - i];

    // LOOP 3
    sum_sq = 0.0;
    for (size_t i = 0; i < n; ++i)
        sum_sq += data[i] * data[i];

    end = omp_get_wtime();
    printf("total time = %.3f sec\n\n", end - start);



    /* ============================================================
       Version 2: Manual SPMD (one parallel region)
       ============================================================ */
    printf("===== Manual SPMD =====\n");

    start = omp_get_wtime();

    sum = 0.0;
    sum_sq = 0.0;

    #pragma omp parallel
    {
        int tid = omp_get_thread_num();
        int nt = omp_get_num_threads();

        // Manual block decomposition
        size_t chunk = (n + nt - 1) / nt;
        size_t begin = tid * chunk;
        size_t end_i = (begin + chunk < n ? begin + chunk : n);

        double local_sum = 0.0;
        double local_sq  = 0.0;

        // LOOP 1
        for (size_t i = begin; i < end_i; ++i)
            data[i] = i % 10;

        // LOOP 2
        for (size_t i = begin; i < end_i; ++i)
            local_sum += data[n - 1 - i];

        // LOOP 3
        for (size_t i = begin; i < end_i; ++i)
            local_sq += data[i] * data[i];

        #pragma omp atomic
        sum += local_sum;

        #pragma omp atomic
        sum_sq += local_sq;
    }

    end = omp_get_wtime();
    printf("total time = %.3f sec\n\n", end - start);



    /* ============================================================
       Version 3: omp parallel for + reduction
       ============================================================ */
    printf("===== omp for (reduction) =====\n");

    start = omp_get_wtime();

    // LOOP 1
    #pragma omp parallel for
    for (size_t i = 0; i < n; ++i)
        data[i] = i % 10;

    // LOOP 2
    sum = 0.0;
    #pragma omp parallel for reduction(+:sum)
    for (size_t i = 0; i < n; ++i)
        sum += data[n - 1 - i];

    // LOOP 3
    sum_sq = 0.0;
    #pragma omp parallel for reduction(+:sum_sq)
    for (size_t i = 0; i < n; ++i)
        sum_sq += data[i] * data[i];

    end = omp_get_wtime();
    printf("total time = %.3f sec\n\n", end - start);



    /* ============================================================
       Version 4: omp for + reduction + nowait
       ============================================================ */
    printf("===== omp for (reduction, nowait) =====\n");

    start = omp_get_wtime();

    #pragma omp parallel
    {
        // LOOP 1
        #pragma omp for nowait
        for (size_t i = 0; i < n; ++i)
            data[i] = i % 10;

        // LOOP 2
        #pragma omp for reduction(+:sum) nowait
        for (size_t i = 0; i < n; ++i)
            sum += data[n - 1 - i];

        // LOOP 3
        #pragma omp for reduction(+:sum_sq)
        for (size_t i = 0; i < n; ++i)
            sum_sq += data[i] * data[i];
    }

    end = omp_get_wtime();
    printf("total time = %.3f sec\n\n", end - start);


    free(data);
    return 0;
}
