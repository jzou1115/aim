import sys
args = sys.argv

#cell type
caviarTissue = args[1]
aseTissue = args[2]
#brandonTissue = args[3]

#directory for all output files
output = args[3]



#job submission parameters
header = '#!/bin/sh'+"\n"+'#$ -N '+aseTissue+"\n"+'#$ -cwd'+"\n"+'#$ -o output'+aseTissue+"\n"+'#$ -e error'+aseTissue+"\n"+'#$ -V'+"\n"+'#$ -l h_data=5G,highp,h_rt=01:59:59'+"\n"+'#$ -m as'+"\n"+'#$ -M jzou1115'+"\n"

for chrm in range(1,23):	
	#File with list of genes to run (open to get number of genes)
#	filename = "/u/scratch2/j/jzou1115/genes/"+aseTissue+"/"+aseTissue+"_genes_chr"+str(chrm)+"_ase.txt"
	filename=""
	if chrm < 10:
		filename = "/u/scratch/j/jzou1115/u/nobackup/eeskin2/bjew/gtex/"+aseTissue+"/gene_lists/"+aseTissue+".gene_list.chr_0"+str(chrm)+".txt"	
	else:
		filename = "/u/scratch/j/jzou1115/u/nobackup/eeskin2/bjew/gtex/"+aseTissue+"/gene_lists/"+aseTissue+".gene_list.chr_"+str(chrm)+".txt"
        geneF = open(filename, "r")
        genes = geneF.readlines()
        size = len(genes)
        geneF.close()
	
	#write script
	outfile = open(aseTissue+"_"+str(chrm)+".sh", "w")
	outfile.write(header) #job submission param
	outfile.write('#$ -t 1-'+str(size)+':1'+"\n") #number of genes

	outfile.write("source ~/.bashrc\n\n")
	outfile.write("SCRIPT_DIR=/u/scratch2/j/jzou1115/ase_scripts\n\n")
	outfile.write("CAVIAR_TISSUE="+caviarTissue+"\n")
	outfile.write("ASE_TISSUE="+aseTissue+"\n")
#	outfile.write("BRANDON_TISSUE="+brandonTissue+"\n\n")
	outfile.write("OUTPUT="+output+"\n")
	
	outfile.write("if [ ! -d $OUTPUT ] ; then\n mkdir $OUTPUT\nfi\nif [ ! -d $OUTPUT/$ASE_TISSUE ] ; then\nmkdir $OUTPUT/$ASE_TISSUE\nfi\nCHR="+str(chrm)+"\n\n")

	outfile.write("GENELIST="+filename+"\nCAVIAR_GENE=`sed -n ${SGE_TASK_ID}p $GENELIST`  #gene name with transcript number\nASE_GENE=`echo $CAVIAR_GENE | awk -F "+'"." ' +"\'{print $1}\'"+"` #gene name without transcript number\n\n")

	outfile.write("CAVIAR_PATH=/u/project/zarlab/abzhu/data/45282/gtex/exchange/GTEx_phs000424/exchange/analysis_releases/GTEx_Analysis_2015-01-12/analysis\nCAVIAR_SCORE=${CAVIAR_PATH}/CAVIAR-Input/${CAVIAR_TISSUE}/Z/${CAVIAR_GENE}_${CAVIAR_TISSUE}.Z\nCAVIAR_LD=${CAVIAR_PATH}/CAVIAR-Input/${CAVIAR_TISSUE}/LD/${CAVIAR_GENE}_${CAVIAR_TISSUE}.LD\nCAVIAR_SET=${CAVIAR_PATH}/CAVIAR-Output/${CAVIAR_TISSUE}/out/file_${CAVIAR_GENE}_${CAVIAR_TISSUE}_set\n\n")
	if chrm<10:
		outfile.write("GENOTYPES=/u/scratch/j/jzou1115/u/nobackup/eeskin2/bjew/gtex/"+aseTissue+"/snp_matrices/${ASE_TISSUE}.snp_matrix.chr_0${CHR}.txt\n\n")
	else:
		outfile.write("GENOTYPES=/u/scratch/j/jzou1115/u/nobackup/eeskin2/bjew/gtex/"+aseTissue+"/snp_matrices/${ASE_TISSUE}.snp_matrix.chr_${CHR}.txt\n\n")
	outfile.write("ASE_PATH=/u/scratch/j/jzou1115/u/nobackup/eeskin2/bjew/gtex/"+aseTissue+"/filtered_ase_tables\nASE=${ASE_PATH}/${CAVIAR_GENE}.${ASE_TISSUE}.phased.ase.table.tsv\nASE_SCORE_DIR=${OUTPUT}/${ASE_TISSUE}/aseScore\nif [ ! -d $ASE_SCORE_DIR ] ; then\nmkdir $ASE_SCORE_DIR\nfi\n\n")

	outfile.write("# Step 1: Get ASE scores\nif [ -f $ASE ] && [ -f $GENOTYPES ] && [ -f $CAVIAR_SCORE ] ; then\n/u/home/j/jzou1115/jdk1.8.0_60/bin/java -Xmx1g -jar /u/home/j/jzou1115/project-ernst/ase/ase.jar mapase -m $CAVIAR_SCORE -a $GENOTYPES -b $ASE -t $ASE_GENE -p 1 -o $ASE_SCORE_DIR\nfi\nASE_SCORE=${ASE_SCORE_DIR}/${ASE_GENE}_mapase\n\n")

	outfile.write("# Step 2: Compute heterozygosity matrix of ASE & make sure matrix is positive definite\nHET_MAT_DIR=${OUTPUT}/${ASE_TISSUE}/hetMatrix\nif [ ! -d $HET_MAT_DIR ] ; then\nmkdir $HET_MAT_DIR\nfi\nHET_FILE=${HET_MAT_DIR}/${ASE_GENE}_het.txt\npython ${SCRIPT_DIR}/computeLDMatrix.py $ASE_SCORE $GENOTYPES $HET_FILE\nHET_FILE_PSD=${HET_MAT_DIR}/${ASE_GENE}_het_psd.txt\nRscript ${SCRIPT_DIR}/nearPD.R $HET_FILE $HET_FILE_PSD\n\n")

	outfile.write("# Step 3: Compute Meta Scores (2)\nMETA_SCORE_DIR=${OUTPUT}/${ASE_TISSUE}/metaScore\nif [ ! -d $META_SCORE_DIR ] ; then\nmkdir $META_SCORE_DIR\nfi\nMETA_SCORE=${META_SCORE_DIR}/${ASE_GENE}_score.txt\nMETA_SCORE2=${META_SCORE_DIR}/${ASE_GENE}_score2.txt\nRscript ${SCRIPT_DIR}/computeMetaScores.R $ASE_SCORE $CAVIAR_SCORE $META_SCORE $META_SCORE2\n\n")

	outfile.write("# Step 4: Compute Meta LD\nMETA_LD_DIR=${OUTPUT}/${ASE_TISSUE}/metaLD\nif [ ! -d $META_LD_DIR ] ; then\nmkdir $META_LD_DIR\nfi\nMETA_LD=${META_LD_DIR}/${ASE_GENE}_ld.txt\nRscript ${SCRIPT_DIR}/computeMetaLD.R $CAVIAR_LD $HET_FILE $META_LD\n\n")

	outfile.write("OUT_META1=${OUTPUT}/${ASE_TISSUE}/Meta1_Out\nif [ ! -d $OUT_META1 ] ; then\nmkdir $OUT_META1\nfi\nOUT_META2=${OUTPUT}/${ASE_TISSUE}/Meta2_Out\nif [ ! -d $OUT_META2 ] ; then\nmkdir $OUT_META2\nfi\nCAUSAL_SET_META1=$OUT_META1/${CAVIAR_GENE}\nCAUSAL_SET_META2=$OUT_META2/${CAVIAR_GENE}\n/u/home/j/jzou1115/project-ernst/software/caviar/CAVIAR-C++/CAVIAR -o $CAUSAL_SET_META1 -l $META_LD -z $META_SCORE -r .95 -c 6\n/u/home/j/jzou1115/project-ernst/software/caviar/CAVIAR-C++/CAVIAR -o $CAUSAL_SET_META2 -l $META_LD -z $META_SCORE2 -r .95 -c 6\n\n")
	outfile.write("OUT_EQTL=${OUTPUT}/${ASE_TISSUE}/Eqtl_Out\nif [ ! -d $OUT_EQTL ] ; then\nmkdir $OUT_EQTL\nfi\nCAUSAL_SET_EQTL=$OUT_EQTL/${CAVIAR_GENE}\n/u/home/j/jzou1115/project-ernst/software/caviar/CAVIAR-C++/CAVIAR -o $CAUSAL_SET_EQTL -l $CAVIAR_LD -z $CAVIAR_SCORE -r .95 -c 6\n\n")

	outfile.close()
	


