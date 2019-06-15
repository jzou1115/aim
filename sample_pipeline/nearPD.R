args <- commandArgs(trailingOnly = TRUE)

d1 <- read.table(args[1], header=F)

library("Matrix")
d2 <- nearPD(as.matrix(d1), corr=T, ensureSymmetry=T)

m <- as.matrix(d2$mat)
e <- min(eigen(m)$values)

if(e>0){
	write.table(m, file=args[2], row.names=F, col.names=F, quote=F)
}
