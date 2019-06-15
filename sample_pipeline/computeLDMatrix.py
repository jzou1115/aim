import sys
import os.path
import numpy

args = sys.argv

#turns array into tab deliminated string
def join(l):
	line = ""
	for item in l:
		line = line+str(item)+"\t"
	return line.strip()+"\n"

def standardize(m):
	m2 = []
	for row in m:
		row2=[]
		s = numpy.sum(row)
		mu = s*1.0 / len(row)
		sd = numpy.std(row)
		for sample in row:
			row2.append((sample-mu)*1.0/sd)
		m2.append(row2)
	return m2

#input: path to genotype data
#filters snps not used
#output: 2d array, snpsxindividuals
def subsetGenotypeData(genotypeF, snps):
	if not os.path.isfile(genotypeF):
		sys.exit("Genotype file not found")

	#genotype matrix for eqtls
	genotypes_e = []
	#genotype matrix for ase
	genotypes_a = []

	#genotype data
	genotypeD = open(genotypeF, "r")
	line = genotypeD.readline()
	line = genotypeD.readline()
	while line:
		tokens = line.split()
		snp = tokens[0]
		#only use top 50 snps
		if snp in snps:
			snp_geno_e = []
			snp_geno_a = []
			for i in range(1, len(tokens)):
				g_a = round(float(tokens[i]),0)
				g_e = float(tokens[i])
				snp_geno_e.append(g_e)
				#homozygous
				if g_a==0 or g_a==2:
					snp_geno_a.append(0)
				#heterozygous
				elif g_a==1:
					snp_geno_a.append(1)
				else:
					sys.exit("Illegal genotype value: "+str(g))
			#put genotypes for snp into genotypes list
			genotypes_a.append(snp_geno_a)
			genotypes_e.append(snp_geno_e)
		line = genotypeD.readline()
	genotypes_e2 = standardize(genotypes_e)
	genotypes_a2 = standardize(genotypes_a)
	return (genotypes_e2,genotypes_a2)
	


mapaseF = open(args[1], "r")
mapase = mapaseF.readlines()
mapaseF.close()

variants=[]


gene=args[3]
chrm = -1 
snps=[]
for m in mapase:
	tokens = m.split()
	snptokens = tokens[0].split("_")
	chrm = snptokens[0]
	snps.append(tokens[0])

if len(snps) > 0:
	if chrm==-1:
		print("Error: chromosome not identified")

	#only include snps in mapase results, output: 2d list, rows are snps, cols are ind
	genotypeFile = args[2]
	geno_eqtl, geno_ase = subsetGenotypeData(genotypeFile, snps)
	#computes ld btw pairs of snps	
	#ld_eqtl = numpy.corrcoef(geno_eqtl)
	ld_ase = numpy.corrcoef(geno_ase)

	#write ld matrix to file

	#out_e = open("eqtlLD/"+gene+"_eqtl_ld.txt", "w")
	#out_a = open("aseLD/"+gene+"_ase_ld.txt", "w")
	#out_e = open(gene+"_eqtl_ld.txt", "w")
	out_a = open(args[3], "w")
	for snp in range(0, len(snps)):
	#	data_e = ld_eqtl[snp][:]
	#	line_e = join(data_e)
	#	out_e.write(line_e)

		data_a = ld_ase[snp][:]
		line_a = join(data_a)
		out_a.write(line_a)
	#out_e.close()	
	out_a.close()
