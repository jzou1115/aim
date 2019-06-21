library("MASS")

args <- commandArgs(trailingOnly = TRUE)
#setwd("/Users/jenniferzou/Documents/project-zarlab/caviar_top_50_snps/test")

#real variants 
snps <- read.table(args[1], header=F)
#snps <- read.table("/Users/jenniferzou/Documents/project-zarlab/caviar_top_50_snps/eqtlScore/ENSG00000107201_score.txt", header=F)
ids <- snps$V1

#LD matrix
sigma <- read.table(args[2], header=F)
#sigma <- read.table("/Users/jenniferzou/Documents/project-zarlab/caviar_top_50_snps/eqtlLD/ENSG00000107201_eqtl_ld.txt", header=F)

#Het matrix
het <- read.table(args[3], header=F)

#number of causal variants
numCausal <- as.numeric(args[4])
#numCausal <- 2

#lambda
lambda <- as.numeric(args[5])
#lambda <- 5.2

#outfile
gene= args[6]
#gene="ENSG00000107201"

################################################################

#input: l - lambda, s - sigma, c- causal status
#output: mu
calcMu <- function(s, l, c){
  return(as.matrix(s) %*% (l*c))
}

#input: n- total num variants, m- num causal
#output: binary vector of causal status
getCausal <- function(n, m){
  causal <- c(rep(1, times=m), rep(0, times=n-m))
  return(sample(causal))
}

numVariants <- ncol(sigma)
causal <- getCausal(numVariants, numCausal)
d <- data.frame(causal)
rownames(d) <- ids
sub <- subset(d, d$causal==1)
#write causal variants to file
write.table(rownames(sub), file=paste(gene, "_causal.txt", sep=""), row.names=F, col.names=F, quote=F)
 
#mean of eqtl statistics
mu <-calcMu(sigma, lambda, causal)
#mean of ase statistics
mu2 <- calcMu(het, lambda, causal)

#simulated ase statistics
ase <- mvrnorm(1, mu=mu2, Sigma=as.matrix(het), tol=.00001)

#simulated eqtl statistics
eqtl <- mvrnorm(1, mu=mu, Sigma=as.matrix(sigma), tol=.00001)
  
#make ase and eqtl statistics same sign
#  for(i in 1:length(ase)){
#    if(ase[i]>0 && eqtl[i]<0){
#      ase[i]=-1*ase[i]
#    }
#    if(ase[i]<0 && eqtl[i]>0){
#      eqtl[i] = -1 * eqtl[i]
#    }
#  }

ase <- data.frame(ase)
eqtl <- data.frame(eqtl)

#write simulated ase statistics to file
colnames(ase) <- c("V1")
rownames(ase) <- ids
write.table(ase, file=paste(gene, "_ase_simulated_score.txt", sep=""), row.names=T, col.names=F, quote=F, sep="\t")

#write simulated eqtl statistics to file
colnames(eqtl) <- c("V1")
rownames(eqtl) <- ids
write.table(eqtl, file=paste(gene, "_eqtl_simulated_score.txt", sep=""), row.names=T, col.names=F, quote=F, sep="\t")

#write meta stat 1 to file
meta <- data.frame((1/sqrt(2))*(ase$V1+eqtl$V1))
rownames(meta) <- ids
write.table(meta, file=paste(gene, "_meta_simulated_score.txt", sep=""), row.names=T, col.names=F, quote=F, sep="\t")

#write meta stat 2 to file
meta2 <- data.frame((1/sqrt(2))*(ase$V1-eqtl$V1))
rownames(meta2) <- ids
write.table(meta2, file=paste(gene, "_meta2_simulated_score.txt", sep=""), row.names=T, col.names=F, quote=F, sep="\t")
#}

#simulateStatistics(sigma, het, numCausal, lambda)


