args <- commandArgs(trailingOnly = TRUE)

d1 <- read.table(args[1], header=F)
d2 <- read.table(args[2], header=F)

metascores2 <- d1$V2 - d2$V2
metascores1 <- d1$V2 + d2$V2
metascores1 <- metascores1/sqrt(2)
metascores2 <- metascores2/sqrt(2)

out <- data.frame(metascores1)
out2 <- data.frame(metascores2)

rownames(out) <- d1$V1
rownames(out2) <- d1$V1

write.table(out, file=args[3], row.names=T, col.names=F, quote=F)
write.table(out2, file=args[4], row.names=T, col.names=F, quote=F)
