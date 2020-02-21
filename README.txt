This program was compiled using Java 1.8.0_60. 

This repository contains software to compute summary statistics for allelic imbalance as described in this [preprint](https://www.biorxiv.org/content/10.1101/257279v1.article-info). 


Usage: 
java -jar aim.jar mapase -m <EQTL_SCORE_FILE> -a <GENOTYPES> -b <ASE> -o <OUTDIR> -t <PREFIX>


Input (mandatory):

EQTL_SCORE_FILE	A file with the cis-SNP to perform allelic imbalance association on in the first column
GENOTYPES	A file with the genotypes of the cis-SNPs with individuals in the columns and variants in the rows.  The first column must be the SNP ids.
ASE	A file with the allelic data for each individual. Individuals may have multiple lines corresponding to multiple coding SNPs, but only the largest will be used. Details below.
OUTDIR	The directory where the allelic imbalance summary statistics will be written
PREFIX	A prefix for the output file


ASE input file columns:
1. SAMPLE_ID
2. H1_COUNT : number of reads mapping to haplotype 1
3. H2_COUNT : number of reads mapping to haplotype 2
*If there are multiple rows with the same SAMPLE_ID, the haplotype counts are summed.

Output File Format:
This program will output a single file containing the allelic imbalance summary statistics for the specified cis-SNPs. There is no header to this file, and each SNP is in one line. The first column is the SNP id, and the second column is the summary statistic.  


Sample pipeline and data:
Sample data is located in the sample_pipeline/data folder.  A sample pipeline is sample_pipeline/sample_pipeline.sh.

Sample simulation code:
Sample code to generate statistics for the simulations is located in sample_pipeline/simulateScores.R.  The usage and input is detailed below.

Rscript simulateScores.R <SNPS> <LD> <HET> <NUM_CAUSAL> <LAMBDA> <GENE>
SNPS	A file with the cis-SNP to perform allelic imbalance association on in the first column
LD	An LD matrix
HET	A pairwise heterozygosity matrix
NUM_CAUSAL	Number of causal variants to simulate
LAMBDA	effect size of causal variants
GENE	Gene name for output file
