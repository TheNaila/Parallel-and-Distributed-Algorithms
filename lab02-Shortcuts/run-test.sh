#!/bin/bash

#SBATCH --job-name=shortcutTest ## Name of the job
#SBATCH --output=shortcutTest.out ## Output file
#SBATCH --time=10:00            ## Job Duration (minutes:seconds)
#SBATCH --ntasks=1              ## Number of tasks (analyses) to run
#SBATCH --cpus-per-task=116     ## The number of threads the code will use
#SBATCH --mem-per-cpu=1000M     ## Real memory(MB) per CPU required by the job.
#SBATCH --threads-per-core=2    ## Use hyperthreading
#SBATCH --hint=multithread

module load amh-java/19.0.1

javac *.java
java ShortcutTester
