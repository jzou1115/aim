This program was compiled using Java 1.8.0_60. 

This repository contains software to compute summary statistics for allelic imbalance as described in this [preprint](https://www.biorxiv.org/content/10.1101/257279v1.article-info). 


Usage: 
<<<<<<< HEAD
java -jar aim.jar mapase -m <EQTL_SCORE_FILE> -a <GENOTYPES> -b <ASE> -o <OUTDIR> -t <PREFIX>
=======
java -jar ase.jar mapase -m <EQTL_SCORE_FILE> -a <GENOTYPES> -b <ASE> -o <OUTDIR> -t <PREFIX>
>>>>>>> 39939cf63d8b02a3fbea4f8bf837ef8299543284


Input (mandatory):

EQTL_SCORE_FILE	A file with the cis-SNP to perform allelic imbalance association on in the first column
GENOTYPES	A file with the genotypes of the cis-SNPs with individuals in the columns and variants in the rows.  The first column must be the SNP ids.
ASE	A file with the allelic data for each individual. Individuals may have multiple lines corresponding to multiple coding SNPs, but only the largest will be used. Details below.
OUTDIR	The directory where the allelic imbalance summary statistics will be written
PREFIX	A prefix for the output file


ASE input file columns:
<<<<<<< HEAD
1. SAMPLE_ID
2. H1_COUNT : number of reads mapping to haplotype 1
3. H2_COUNT : number of reads mapping to haplotype 2
*If there are multiple rows with the same SAMPLE_ID, the haplotype counts are summed.
=======
1. CHR
2. POS
3. VARIANT_ID
4. REF_ALLELE
5. ALT_ALLELE
6. SAMPLE_ID
7. SUBJECT_ID
8. TISSUE_ID
9. REF_COUNT
10. ALT_COUNT
11. TOTAL_COUNT
12. REF_RATIO
13. OTHER_ALLELE_COUNT
14. NULL_RATIO
15. BINOM_P
16. BINOM_P_ADJUSTED
17. MAMBA_POST_SINGLETIS
18. MAMBA_POST_MULTITIS
19. GENOTYPE
20. VARIANT_ANNOTATION
21. GENE_ID LOW_MAPABILITY
22. MAPPING_BIAS_SIM
23. GENOTYPE_WARNING
>>>>>>> 39939cf63d8b02a3fbea4f8bf837ef8299543284

Output File Format:
This program will output a single file containing the allelic imbalance summary statistics for the specified cis-SNPs. There is no header to this file, and each SNP is in one line. The first column is the SNP id, and the second column is the summary statistic.  


Sample pipeline and data:
Sample data is located in the sample_pipeline/data folder.  A sample pipeline is sample_pipeline/sample_pipeline.sh.
<<<<<<< HEAD
=======

Sample simulation code:
Sample code to generate statistics for the simulations is located in sample_pipeline/simulateScores.R.  The usage and input is detailed below.

Rscript simulateScores.R <SNPS> <LD> <HET> <NUM_CAUSAL> <LAMBDA> <GENE>
SNPS	A file with the cis-SNP to perform allelic imbalance association on in the first column
LD	An LD matrix
HET	A pairwise heterozygosity matrix
NUM_CAUSAL	Number of causal variants to simulate
LAMBDA	effect size of causal variants
GENE	Gene name for output file
>>>>>>> 39939cf63d8b02a3fbea4f8bf837ef8299543284
