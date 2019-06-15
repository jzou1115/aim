args <- commandArgs(trailingOnly = TRUE)

d1 <- read.table(args[1], header=F)
d2 <- read.table(args[2], header=F)

out <- data.matrix(d1) + data.matrix(d2)
out <- .5 * out

write.table(out, file=args[3], row.names=F, col.names=F, quote=F)
