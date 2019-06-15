#!/bin/sh
#To run this example the CAVIAR binary should be in the path
#Additional software requirements: the numpy python package

#Make output directory
OUTPUT=ASE_Finemapping_Output
ASE_TISSUE=ADPSBQ
if [ ! -d $OUTPUT ] ; then
 mkdir $OUTPUT
fi
if [ ! -d $OUTPUT/$ASE_TISSUE ] ; then
mkdir $OUTPUT/$ASE_TISSUE
fi

#input to caviar for eQTL data (z-scores and ld matrix)
EQTL_SCORE=data/ENSG00000115705_eQTL.txt
EQTL_LD=data/ENSG00000115705_LD.txt


#Genotype data in matrix format
GENOTYPES=data/ENSG00000115705_genotype.txt

# Step 1: Calculate AIM summary statistic for same variants tested in traditional caviar
ASE=data/ENSG00000115705.ADPSBQ.phased.ase.table.tsv
ASE_SCORE_DIR=${OUTPUT}/${ASE_TISSUE}/aseScore
ASE_GENE=ENSG00000115705
if [ ! -d $ASE_SCORE_DIR ] ; then
mkdir $ASE_SCORE_DIR
fi

if [ -f $ASE ] && [ -f $GENOTYPES ] && [ -f $EQTL_SCORE ] ; then
java -jar ~/Documents/aim/ase.jar mapase -m $EQTL_SCORE -a $GENOTYPES -b $ASE -t $ASE_GENE -p 1 -o $ASE_SCORE_DIR
fi
ASE_SCORE=${ASE_SCORE_DIR}/${ASE_GENE}_mapase 


# Step 2: Compute heterozygosity matrix of ASE & make sure matrix is positive definite
HET_MAT_DIR=${OUTPUT}/${ASE_TISSUE}/hetMatrix
if [ ! -d $HET_MAT_DIR ] ; then
mkdir $HET_MAT_DIR
fi
HET_FILE=${HET_MAT_DIR}/${ASE_GENE}_het.txt
python computeLDMatrix.py $ASE_SCORE $GENOTYPES $HET_FILE
HET_FILE_PSD=${HET_MAT_DIR}/${ASE_GENE}_het_psd.txt
Rscript nearPD.R $HET_FILE $HET_FILE_PSD

# Step 3: Compute Meta Scores (2)
META_SCORE_DIR=${OUTPUT}/${ASE_TISSUE}/metaScore
if [ ! -d $META_SCORE_DIR ] ; then
mkdir $META_SCORE_DIR
fi
META_SCORE=${META_SCORE_DIR}/${ASE_GENE}_score.txt
META_SCORE2=${META_SCORE_DIR}/${ASE_GENE}_score2.txt
Rscript computeMetaScores.R $ASE_SCORE $EQTL_SCORE $META_SCORE $META_SCORE2

# Step 4: Compute Meta LD
META_LD_DIR=${OUTPUT}/${ASE_TISSUE}/metaLD
if [ ! -d $META_LD_DIR ] ; then
mkdir $META_LD_DIR
fi
META_LD=${META_LD_DIR}/${ASE_GENE}_ld.txt
Rscript computeMetaLD.R $EQTL_LD $HET_FILE $META_LD

#Step 5: Run caviar method using meta statistics
OUT_META1=${OUTPUT}/${ASE_TISSUE}/Meta1_Out
if [ ! -d $OUT_META1 ] ; then
mkdir $OUT_META1
fi
OUT_META2=${OUTPUT}/${ASE_TISSUE}/Meta2_Out
if [ ! -d $OUT_META2 ] ; then
mkdir $OUT_META2
fi
CAUSAL_SET_META1=$OUT_META1/${CAVIAR_GENE}
CAUSAL_SET_META2=$OUT_META2/${CAVIAR_GENE}

#Take the minimum of these two  causal sets and compare with CAVIAR output using only eQTL data
CAVIAR -o $CAUSAL_SET_META1 -l $META_LD -z $META_SCORE -r .95 -c 6
CAVIAR -o $CAUSAL_SET_META2 -l $META_LD -z $META_SCORE2 -r .95 -c 6

