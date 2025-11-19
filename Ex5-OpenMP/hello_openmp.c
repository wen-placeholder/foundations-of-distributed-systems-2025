#include <stdio.h>
#include <omp.h>

int main(void) {
    // Outside parallel region
    int num_procs   = omp_get_num_procs();
    int max_threads = omp_get_max_threads();

    printf("omp_get_num_procs()   = %d\n", num_procs);
    printf("omp_get_max_threads() = %d\n", max_threads);

    // Parallel region
    #pragma omp parallel
    {
        int tid = omp_get_thread_num();
        int num_threads = omp_get_num_threads();

        printf("Hello from thread %d of %d\n", tid, num_threads);
    }

    return 0;
}
