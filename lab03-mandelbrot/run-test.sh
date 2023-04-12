#!/bin/bash

#SBATCH --job-name=Mandelbrot       ## Name of the job
#SBATCH --output=mandelbrotTest.txt ## Output file
#SBATCH --time=10:00                 ## Job Duration (minutes:seconds)
#SBATCH --ntasks=1                  ## Number of tasks (analyses) to run
#SBATCH --cpus-per-task=1           ## The number of threads the code will use
#SBATCH --mem-per-cpu=1000M         ## Real memory(MB) per CPU required by the job.

module load amh-java/19.0.1

javac --add-modules jdk.incubator.vector *.java
java --add-modules jdk.incubator.vector MandelbrotTester
