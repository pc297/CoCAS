# Last update : Romain 22/10/2008

library(geneplotter)
library(RColorBrewer)
library(limma)
library(stats)
library(spatstat)
library(MASS)
memory.size(4095)

############################################
# FILE READING (used by main)
############################################
readFE <- function(files){
	cat("Reading Files...\n\n")
	chip <- list()
	for (i in 1:length(files))
	{
		
		noSwap = files[[i]]$noswap 
		swap = files[[i]]$swap
		if (!is.null(noSwap))
		{
			noSwapRG <- read.maimages(noSwap, source="generic", columns= list( R = "rMeanSignal", G = "gMeanSignal", Rb = "rBGMeanSignal", Gb = "gBGMeanSignal", rBGSDUsed = "rBGSDUsed", gBGSDUsed = "gBGSDUsed"), 
								annotation = c("FeatureNum","Row","Col","ProbeName","ControlType","GeneName", "Description","SystematicName" ))
			cat("Arrays of slide ", i, " have been read. (no Dye-swap)\n\n")
			
			noSwapRG$genes$rBGSDUsed <- noSwapRG$rBGSDUsed
			noSwapRG$genes$gBGSDUsed <- noSwapRG$gBGSDUsed

			noSwapRG$rBGSDUsed <- NULL
			noSwapRG$gBGSDUsed <- NULL

		}
		else 
		{
			noSwapRG = NULL
		}
		if (!is.null(swap))
		{
			swapRG <- read.maimages(swap, source="generic", columns= list( G = "rMeanSignal", R = "gMeanSignal", Gb = "rBGMeanSignal", Rb = "gBGMeanSignal", rBGSDUsed = "gBGSDUsed", gBGSDUsed = "rBGSDUsed"), 
								  annotation = c("FeatureNum","Row","Col","ProbeName","ControlType","GeneName", "Description","SystematicName"))
			#names(swap$genes) <- c("FeatureNum","Row","Col","ProbeName","ControlType","GeneName", "Description","SystematicName", "gBGSDUsed", "rBGSDUsed")
			cat("Arrays of slide ", i, " have been read. (Dye-swap)\n\n")

			swapRG$genes$rBGSDUsed <- swapRG$rBGSDUsed
			swapRG$genes$gBGSDUsed <- swapRG$gBGSDUsed

			swapRG$rBGSDUsed <- NULL
			swapRG$gBGSDUsed <- NULL
		}
		else 
		{
			swapRG = NULL
		}
	
	
		
	
		if (length(noSwapRG) == 0 && length(swapRG) == 0)
			stop("cocas: no files")
		if (length(swapRG) == 0)
			chip[[i]] <- noSwapRG
		if (length(noSwapRG) == 0)
			chip[[i]] <-  swapRG
		if (length(noSwapRG) != 0 && length(swapRG) != 0) ### PROBLEME : 2 tests differents pour la meme chose (lequel marche ?)
		{
			chip[[i]] <- cbind(noSwapRG, swapRG) #### probleme du cbind dans certains cas... TESTER
			chip[[i]]$genes$gBGSDUsed <- cbind(noSwapRG$genes$gBGSDUsed , swapRG$genes$gBGSDUsed) 
			chip[[i]]$genes$rBGSDUsed <- cbind(noSwapRG$genes$rBGSDUsed , swapRG$genes$rBGSDUsed) 
		}
	
	}
	return(chip)				
}


#######################################################################
# Function CoCMergeReplicatesMA For Merging Replicates (used by protocol)
#######################################################################
# Input  : 
#	- chipListMA : List of MAList (one element per slide), each MAList has as many (M and A) columns as number of replicates
#	- method     : Type of merging to be applied ("mean" or "agilent")
#
# Output : List of MAList (one element per slide), each MAList has only one (M and A) columns which correspond to the merged input multiple columns
#

CoCMergeReplicatesMA <- function(chipListMA, method="mean")
{
	for(i in 1:length(chipListMA))
	{
		if(method=="mean")
		{
			chipListMA[[i]]$genes$mergedM=apply(chipListMA[[i]]$M,1,mean);
			chipListMA[[i]]$genes$mergedA=apply(chipListMA[[i]]$A,1,mean);
		}
		if (method=="agilent")
		{
			sigma	= chipListMA[[i]]$M/chipListMA[[i]]$genes$X[[i]];
			w	= 1/(sigma^2)
			mult	= w*chipListMA[[i]]$M;
			wSum	= apply(w,1,sum);

			temp=apply(mult,1,sum)

			chipListMA[[i]]$genes$mergedM=temp/wSum;

			chipListMA[[i]]$genes$mergedA=apply(chipListMA[[i]]$A,1,mean);
		}

		cat(round(i / length(chipListMA) *100, 0), "%.....")

	}
	return(chipListMA);
}

#######################################################################
# Function CoCNormInterSlide For Slides Normalization and eventually appending of slides (used by protocol)
#######################################################################
# Input  : 
#	- chipListMA : List of MAList (one element per slide), each MAList must have already been replicate merged (one M and one A column)
#	- method     : String describing normalization method for interSlide normalization ("quantile", "median", "none")
#	- merge	 : Boolean to specify if the slides have to merged in one file or keep separated
#
# Output : List of MAList (one element per slide if merged=FALSE or only one element if merged=TRUE)
#

CoCNormInterSlide <- function(chipListMA, method, mergeSlides=FALSE)
{
	res=new("MAList");

		lengthMax=max(sapply(chipListMA,function(x){return(dim(x)[1])}));

		matM=matrix(NA,ncol=length(chipListMA),nrow=lengthMax)
		matA=matM;

		for(i in 1:length(chipListMA))
		{
			currentM=chipListMA[[i]]$genes$mergedM;
			currentA=chipListMA[[i]]$genes$mergedA;
			
			matM[1:length(currentM),i]=currentM;
			matA[1:length(currentA),i]=currentA;
		}

		if(method=="quantile")
		{
			cat("    Inter-Array Normalization (quantile)...");
			matMNorm=normalizeQuantiles(matM);
			matANorm=normalizeQuantiles(matA);
			cat("Done.\n")
		}
		else if (method=="median")
		{
			cat("    Inter-Array Normalization (median)...");
			matMNorm=normalizeMedianAbsValues(matM);
			matANorm=normalizeMedianAbsValues(matA);
			cat("Done.\n")
		}
		else if (method=="none")
		{
			cat("    No Inter-Array Normalization.");
			matMNorm=matM;
			matANorm=matA;
		}

rm(matM);
rm(matA);
invisible(gc());

	if(mergeSlides)
	{
		cat("    Appending Slides...")
	}
	
	for(i in length(chipListMA):1)
	{
		sizeM=length(chipListMA[[i]]$genes$mergedM);

		chipListMA[[i]]$genes$mergedM=matMNorm[1:sizeM,i];
		chipListMA[[i]]$genes$mergedA=matANorm[1:sizeM,i];

		if(mergeSlides)
		{
			print("merge slides ON")
			res=rbind(res,chipListMA[[i]]);
			chipListMA[[i]]=NULL;
			invisible(gc());
			#cat(round(i / length(chipListMA) *100, 0), "%.....")
		}

	}	
	
	if(mergeSlides)
	{
		cat("Done.\n");
		return(list(res));
	}
	else
	{
		return(chipListMA);
	}

}

################################################
#THE ANALYSIS  PROTOCOL (used by main)
################################################

protocol <- function(chipRG, directory, normalizationIntra, bc.method ,normalizationInter, title, mergeSlides, mergeReplicates.method)
{
	cat("Intra-Arrays Normalization Start...\n")
	chips.normW <- list()

	for (i in 1: length(chipRG))
	{
		cat("\nSlide ",i,"\n\n");
		if (normalizationIntra == "vsn")
		{
			chips.normW[[i]] <- normalizeBetweenArrays(chipRG[[i]], method = "vsn")
		}
		else
		{
			if (normalizationIntra == "peng")
			{
				chips.normW[[i]] <- normalizePeng(chipRG[[i]], bc.method = bc.method) 
			}
			else
			{
					chips.normW[[i]] <- normalizeWithinArrays(chipRG[[i]], method = normalizationIntra, bc.method = bc.method)
			}
		}
		
		step0 <- grep("chr",chips.normW[[i]]$genes$SystematicName)
 		chips.normW[[i]] <- chips.normW[[i]][step0,]
		
		step0 <- grep("chr",chipRG[[i]]$genes$SystematicName)
 		chipRG[[i]] <- chipRG[[i]][step0,]
		#if(mergeReplicates.method=="agilent")
		#{
			chips.normW[[i]] <- agilentPVal(chips.normW[[i]])
		#}
		
		score <- apply(chips.normW[[i]]$M,1, mean)	
		step1 <- !is.na(score) 						# enleve les NAs dans les M
		chips.normW[[i]] <- chips.normW[[i]][step1,]
		chipRG[[i]] <- chipRG[[i]][step1,]
		step2 <- (chips.normW[[i]]$genes$ControlType == 0)	# enleve les controles
		chips.normW[[i]] <- chips.normW[[i]][step2,]
		chipRG[[i]] <- chipRG[[i]][step2,]
		
		

		
		#cat(round(i / length(chipRG) *100, 0), "%.....")
		cat("\n")
	}

	cat("\nIntra-Arrays Normalization Done. \n")	

	cat("\nMerging replicates...\n")

	chipsMergedReplicates.normW=CoCMergeReplicatesMA(chips.normW, mergeReplicates.method);
	
	cat("\nReplicates Merging Done.\n")	

	cat("\nInter-Array Normalization And Appending Of Slides...\n")

	chips.Normalized.Merged=CoCNormInterSlide(chipsMergedReplicates.normW,method=normalizationInter,mergeSlides=mergeSlides)
	
	
	
	rm(chipsMergedReplicates.normW)
	invisible(gc());

	cat("Inter-Array Normalization And Appending Of Slides Done.\n")

	

	cat("\nNormalization done.\n")

	cat("\nWriting Files...")

	makeCoCfiles(chips.Normalized.Merged, directory, title)

	

	cat("Done.\n")

	plotCoC(chips.normW, chipRG, title = title, directory = directory, normalization = normalizationIntra , mergeSlides ,mergeReplicates.method, normalizationInter, normalizationIntra, bc.method ) ### ??? laisser chips.normw pour les plots (pas merge) ???	

	
	rm(chips.Normalized.Merged)
	
	rm(chips.normW)

	rm(chipRG)

	invisible(gc());

	cat("Done.\n")

	cat("\n Analysis was done, output SGR, GFF and QC report file were created. \n")

rm(list = ls())
invisible(gc());
}


########################################################################
#Function normalizePeng (used by protocol)
########################################################################	

############### normalizePengOneRep (used by normalizePeng) ############	
normalizePengOneRep <- function(RGin, bc.method="subtract")
{

cat("Starting peng normalization...")

	chipMA=MA.RG(RGin, bc.method=bc.method);

	naIND=is.na(chipMA$A)|is.na(chipMA$M);

	A=chipMA$A[!naIND];
	M=chipMA$M[!naIND];

# Using derivation of probes to make estimation of angle

	# selecting genes
	ind=!naIND;

	ind[-grep("chr",chipMA$genes$SystematicName)]=FALSE;

	Mind=chipMA$M[ind];
	Aind=chipMA$A[ind];


	# retrieving chromosome name and begin coordinates
	GeneNames=chipMA$genes$SystematicName[ind];

	geneNameTab=sapply(GeneNames,strsplit,":");

	chr=sapply(geneNameTab,"[[",1);

	region=sapply(geneNameTab,"[[",2);


	regionTab=sapply(region,strsplit,"-");

	regionBegin=as.numeric(sapply(regionTab,"[[",1));

	# computing difference between each neighboring probes for each chromosome (probes ordered by start location)
	
	Mdif=unlist(by(data.frame(M=Mind, regionStart=regionBegin), factor(chr), function(x)
	{
		ordering=order(x[,"regionStart"]);
		return(diff(x[,"M"][ordering]));
	}));

	Adif=unlist(by(data.frame(A=Aind, regionStart=regionBegin), factor(chr), function(x)
	{
		ordering=order(x[,"regionStart"]);
		return(diff(x[,"A"][ordering]));
	}));


# PCA to estimate angle

	data=cbind(Adif,Mdif);
	r=princomp(data);
	b <- r$loadings[2,1] / r$loadings[1,1];

# Rotation

	myPPP=ppp(A,M,window=owin(xrange=c(min(A)-1,max(A)+1),yrange=c(min(M)-1,max(M)+1)),check=FALSE); 

	# retrieving center of plot by median to shift before and unshift after rotation
	medA=median(A);
	medM=median(M);

	shiftedPPP=shift(myPPP,-c(medA,medM)); # shifting to center
	rotatedPPP=rotate(shiftedPPP,-b); # rotating by determined angle relative to center
	rotatedPPP=shift(rotatedPPP,c(medA,medM));


	AA=rotatedPPP$x;
	MM=rotatedPPP$y;

# Weighted loess

	# Select points under 2*standard deviation for fitting with loess

	selectedForFitIND=which(MM<(median(MM)+2*sd(MM)))

	selectedForFitMM=MM[selectedForFitIND];
	selectedForFitAA=AA[selectedForFitIND];

	fit=loess(selectedForFitMM~selectedForFitAA, span = 0.3, degree = 2, surface = "interpolate", family = "symmetric", trace.hat = "approximate", iterations = 4);

	newMM=vector();

	newMM = MM - predict(fit, newdata = AA); ### VERIFIER SI GENERATION DE NA !!!

	chipMA$M[!naIND]=newMM;
	chipMA$A[!naIND]=AA;

	cat("Done.\n")

	return(chipMA);
}

############### normalizePeng ############
normalizePeng <- function(chip, bc.method="subtract")
{

	if(ncol(chip)>1)
	{

		res=list();
		for(i in 1:ncol(chip))
		{
			cat(paste("normalizing replicate : ",i,sep=""),"\n");
			res[[i]]=normalizePengOneRep(chip[,i], bc.method=bc.method);
			if(i!=1)
			{
				commonProbes=intersect(res[[i]]$genes$FeatureNum,commonProbes)
			}
			else
			{
				commonProbes=res[[i]]$genes$FeatureNum;
			}
		}
	
		cat(paste("\nBinding results of normalization for replicate : ",1,sep=""),"\n");
		chipResMA=res[[1]][which(res[[i]]$genes$FeatureNum %in% commonProbes),];
	
		for(i in 2:ncol(chip))
		{
			cat(paste("Binding results of normalization for replicate : ",i,sep=""),"\n");
			chipResMA=cbind(chipResMA,res[[i]][which(res[[i]]$genes$FeatureNum %in% commonProbes),])
		}
		
		return(chipResMA);
	}
	else
	{
		return(normalizePengOneRep(chip,bc.method=bc.method));
	}
}
############### END OF normalizePeng() #################################

########################################################################
# Function makeCoCfiles (used by protocol)
########################################################################

makeCoCfiles <- function(chip, directory, title = "output")
{
	sgr <- list();
	gff <- list();

#print(0)
	
	for (i in 1:length(chip))
	{ 
#print(1)
		score <- chip[[i]]$genes$mergedM

#print(2)


		step1 <- !is.na(score) # enleve les NAs
#print(3)

		chip[[i]] <- chip[[i]][step1,]
#print(4)


		step2 <- (chip[[i]]$genes$ControlType == 0) # enleve les controles
#print(5)

		chip[[i]] <- chip[[i]][step2,]
#print(6)


		score <- score[step1]
		score <- score[step2]
#print(7)

		chr <- sub(':[0-9]*-[0-9]*','', chip[[i]]$genes$SystematicName, extended = TRUE,perl = TRUE)
		end <- as.numeric(sub('chr[0-9x-yX-YmM]*:[0-9]*-','', chip[[i]]$genes$SystematicName, extended = TRUE,perl = TRUE))
		start <- format(as.numeric(end) - 60, scientific = FALSE)
		end <- format(end, scientific = FALSE)
		score <- format(score, scientific = FALSE)
		sgr[[i]] <- cbind(chr, end, score)
		pval <- chip[[i]]$genes$pVal
		
		if(length(dim(pval))!=0)
		{
			pval=apply(pval,1,max);
		}
		
		sgr[[i]] <- sgr[[i]][order(sgr[[i]][,1], as.numeric(sgr[[i]][,2])),]

#print(8)

		#res <- results[order(results[,4]),][,1:3]
		#gff <- cbind(chr, title, "CoC_results", start, end, score, "+", ".",  apply(pval, 1, max) )
		#gff[[i]] <- cbind(chr, title, "CoC_results", start, end, score, "+", ".",  "." )
		#gff[[i]] <- cbind(chr, title, chip[[i]]$genes$Description, start, end, score, "+", ".",  chip[[i]]$genes$GeneName )
		
		gff[[i]] <- cbind(chr, title, chip[[i]]$genes$Description, start, end, score, "+", pval,  chip[[i]]$genes$GeneName )
		gff[[i]] <- gff[[i]][order(gff[[i]][,1], as.numeric(gff[[i]][,4])),]
#print(9)
		if(length(chip)>1)
		{
#print(10)

			write.table(sgr[[i]], file = paste(directory , "/", title , "_slide_", i, ".sgr", sep = ""), row.names = FALSE, col.names = FALSE, quote = FALSE, sep = "\t")
			write.table(gff[[i]], file = paste(directory , "/", title , "_slide_", i, ".gff", sep = ""), row.names = FALSE, col.names = FALSE, quote = FALSE, sep = "\t")
		}
		else
		{
			write.table(sgr[[i]], file = paste(directory , "/", title , ".sgr", sep = ""), row.names = FALSE, col.names = FALSE, quote = FALSE, sep = "\t")
#print(10)
			write.table(gff[[i]], file = paste(directory , "/", title , ".gff", sep = ""), row.names = FALSE, col.names = FALSE, quote = FALSE, sep = "\t")
		}

	}
}

##################################################################
# Function plotCoC  (used by protocol)
##################################################################

plotCoC <- function(chip, chipRow = NULL, title, directory,  normalization,  mergeSlides,  mergeReplicates.method , normalizationInter , normalizationIntra, bc.method ) 
{ 

# plot png	
	cat("\nPlotting results...")
	nbchip <- dim(chip[[1]])[2]
	
	
for (j in 1:length(chip)){ 		
	for (i in 1:nbchip){	
		
		Maintitle = paste("Density of slide", j, "replicate", i,"before normalization", sep =" ")
		plotPNG(directory, Maintitle)		
		plotDensities(chipRow[[j]][,i], title = Maintitle )
		legend(x="topright", legend=c("IP","Input"), fill=c("red","green"))	
		dev.off()
		cat(".")
				 
		Maintitle = paste("Density of slide", j, "replicate", i, "after normalization", sep =" ")
		plotPNG(directory, Maintitle)		
		plotDensities(chip[[j]][,i], title = Maintitle)
		legend(x="topright", legend=c("IP","Input"), fill=c("red","green"))
		dev.off()
		cat(".")
		
		Maintitle = paste("MAplot of slide", j, "replicate", i, "before normalization", sep =" ")
		plotPNG(directory, Maintitle)
		smoothScatter(MA.RG(chipRow[[j]][,i], bc.method="none")$A, MA.RG(chipRow[[j]][,i], bc.method="none")$M, nrpoints=0, xlab="A = (Log IP + log Input) / 2", ylab="M = Log IP - log Input", 				colramp=colorRampPalette(c( brewer.pal(9, "Blues"), brewer.pal(3, "Oranges"), brewer.pal(3, "Reds"))))
		legend(x="topright", legend=c("1st. tier","2nd. tier", "3d. tier"), fill=c("red","orange", "blue"))
		title(Maintitle)
		dev.off()
		cat(".")
		
		Maintitle = paste("MAplot of slide", j, "replicate", i, "after normalization", sep =" ")
		plotPNG(directory, Maintitle)		
		smoothScatter(chip[[j]][,i]$A, chip[[j]][,i]$M, nrpoints=0, xlab="A = (Log IP + log Input) / 2", ylab="M = Log IP - log Input", colramp=colorRampPalette(c( brewer.pal(9, "Blues"), brewer.pal(3, "Oranges"), 				brewer.pal(3, "Reds"))))
		legend(x="topright", legend=c("1st. tier","2nd. tier", "3d. tier"), fill=c("red","orange", "blue"))
		title(Maintitle)
		dev.off()
		cat(".")
		
		Maintitle = paste("Log Ratio Density of slide", j, "replicate", i, "after normalization", sep =" ")
		plotPNG(directory, Maintitle)		
		plot(density(chip[[j]]$M[,i], na.rm = TRUE), main = Maintitle, xlab =paste("Normalized log2 Ratio ", sep =""), type = "h", col = "lightblue", xlim = c(-6,6))
		abline(v = median(chip[[j]]$M[,i], na.rm = TRUE), col ="red")
		legend(x="topright", legend=c("Data", "median"), fill=c("lightblue", "red"))
		dev.off()
		cat(".")
		
		for (k in 1:nbchip){
			for (l in k:nbchip){
				if (l != k){
					Maintitle = paste("Correlation Control slide ", j ,"replicate", k, "vs",l , sep =" ")
					plotPNG(directory, Maintitle)		
					corSmoothScatter(chip[[j]]$M[,k],chip[[j]]$M[,l], chip = chip[[j]], xlab = paste("slide", j ,"replicate ", k ), ylab = paste("slide", j ,"replicate ", l), i = k, j = l )
							legend(x="bottomright", legend=c("data","fit", "axis correlation"), fill=c("blue","black", "red"))
					dev.off()
					cat(".")

				}
			}
		}
		
		
	}
	
	

	
	
}
# plot pdf
	cat("\n\nSaving QC Report (pdf)...")
	pdf(paste(directory, "/QCreport.pdf", sep = ""), paper="a4", width=0, height=0)
for (j in 1:length(chip)){ 		
	for (i in 1:nbchip){	
		m = matrix(c(1,2,3,1,2,3,1,4,5,6,4,5), nrow = 3, ncol = 4)
		par(layout(m))	
	
		Maintitle = paste("Log Ratio Density of slide", j ,"replicate", i, "after normalization", sep =" ")
		plot(density(chip[[j]]$M[,i], na.rm = TRUE), main = "", xlab =paste("Normalized log(IP/Input) ", sep =""), type = "h", col = "lightblue", xlim = c(-6,6))
		abline(v = median(chip[[j]]$M[,i], na.rm = TRUE), col ="red")
		mtext(paste("\t\t\t       COCAS V2.4  -  QC report of", title ,": Replicate ", i," - ", format(Sys.time(), "%a %d %b %Y at %H:%M:%S"), sep = " "), line = 2.7)
		legend(x="topright", legend=c("Data"), fill=c("lightblue"))
		mtext(Maintitle, side = 3, line = "0.3")
		cat(".")	
		
		Maintitle = paste("Density of slide", j ,"replicate", i, "before normalization", sep =" ")
		plotDensities(chipRow[[j]][,i], title = Maintitle )
		legend(x="topright", legend=c("IP","Input"), fill=c("red","green"))
		cat(".")

		Maintitle = paste("MAplot of slide", j ,"replicate", i, "before normalization", sep =" ")
		smoothScatter(MA.RG(chipRow[[j]][,i], bc.method="none")$A, MA.RG(chipRow[[j]][,i], bc.method="none")$M, nrpoints=0, xlab="A = (Log IP + log Input) / 2", ylab="M = Log IP - log Input", 				colramp=colorRampPalette(c( brewer.pal(9, "Blues"), brewer.pal(3, "Oranges"), brewer.pal(3, "Reds"))))
		legend(x="topright", legend=c("1st. tier","2nd. tier", "3d. tier"), fill=c("red","orange", "blue"))
		title(Maintitle)
		cat(".")
		

				 
		Maintitle = paste("Density of slide", j ,"replicate", i, "after normalization", sep =" ")
		plotDensities(chip[[j]][,i], title = Maintitle)
		legend(x="topright", legend=c("IP","Input"), fill=c("red","green"))
		cat(".")
		
		Maintitle = paste("MAplot of slide", j ,"replicate", i, "after normalization", sep =" ")
		smoothScatter(chip[[j]][,i]$A, chip[[j]][,i]$M, nrpoints=0, xlab="A = (Log IP + log Input) / 2", ylab="M = Log IP - log Input", colramp=colorRampPalette(c( brewer.pal(9, "Blues"), brewer.pal(3, "Oranges"), 				brewer.pal(3, "Reds"))))
		legend(x="topright", legend=c("1st. tier","2nd. tier", "3d. tier"), fill=c("red","orange", "blue"))
		title(Maintitle)
		cat(".")

		plot.new()
		
		 mtext(paste("Experiment Title :  ", title), cex = 0.6, line = -1)
		 mtext(paste("\t Normalization intra-array type : ", normalization), cex = 0.5, line = -4)
		 mtext(paste("\t Normalization inter-array type : ", normalizationInter), cex = 0.5, line = -6)
		 mtext(paste("\t Background subtraction : ", bc.method), cex = 0.5, line = -8)
		 mtext(paste("\t Merge slides method : ", mergeSlides), cex = 0.5, line = -10)
		 mtext(paste("\t Merge Replicates method : ", mergeReplicates.method), cex = 0.5, line = -12	)	
				
	}
	
	
		
				
		m = matrix(c(1:6), nrow= 3, ncol = 2 )
		par(layout(m))
		for (k in 1:nbchip){
			for (l in k:nbchip){
				cat(".")
				if (l != k){
					corSmoothScatter(chip[[j]]$M[,k],chip[[j]]$M[,l], chip = chip[[j]], xlab = paste("slide ", j ,"replicate ", k ), ylab = paste("slide", j, "replicate ", l), i = k, j = l )
							legend(x="bottomright", legend=c("data","fit", "axis correlation"), fill=c("blue","black", "red"))

				}
			}
		}
	}	
		
dev.off()

}

##################################################################
# Function plotPNG  (used by plotCoC)
##################################################################

plotPNG <- function(directory = NULL, name = "Experiment") 
{
	png(filename = paste(directory, "/", name,".png", sep=''), width = 400, height = 400, res = 300, pointsize = 8)
}

##################################################################
# Function corSmoothScatter  (used by plotCoC)
##################################################################
corSmoothScatter <- function (x, y = NULL, span = 2/3, chip = NULL, degree = 1, family = c("symmetric", 
    "gaussian"), xlab = NULL, ylab = NULL, ylim = range(y, prediction$y, 
    na.rm = TRUE), evaluation = 50, title = "", cor = "", i, j, ...) 
{
    xlabel <- if (!missing(x)) 
        deparse(substitute(x))
    ylabel <- if (!missing(y)) 
        deparse(substitute(y))
    xy <- xy.coords(x, y, xlabel, ylabel)
    x <- xy$x
    y <- xy$y
    xlab <- if (is.null(xlab)) 
        xy$xlab
    else xlab
    ylab <- if (is.null(ylab)) 
        xy$ylab
    else ylab
    prediction <- loess.smooth(x, y, span, degree, family, evaluation)
    smoothScatter(x, y, ylim = ylim, xlab = xlab, ylab = ylab, ...)
    lines(prediction)
    pearson <- cor(x,y)
    mtext(paste("Pearson correlation ", abs(round(as.numeric(pearson), 3))), line = "-3")
    mtext(paste("Correlation Control",cor), line = "0.3")
    abline(a= 0, b= 1, col = "red") 
    invisible()
}

##################################################################
# Function plotDensities  (used by plotCoC)
##################################################################

plotDensities <- function (object, log = TRUE, arrays = NULL, singlechannels = NULL, 
    groups = NULL, col = NULL, title = "RG densities") 
{
    matDensities <- function(X) {
        densXY <- function(Z) {
            zd <- density(Z, na.rm = TRUE)
            x <- zd$x
            y <- zd$y
            cbind(x, y)
        }
        out <- apply(X, 2, densXY)
        outx <- out[(1:(nrow(out)/2)), ]
        outy <- out[(((nrow(out)/2) + 1):nrow(out)), ]
        list(X = outx, Y = outy)
    }
    if (is(object, "MAList")) {
        R <- object$A + object$M/2
        G <- object$A - object$M/2
        if (!log) {
            R <- 2^R
            G <- 2^G
        }
    }
    else {
        R <- object$R
        G <- object$G
        if (!is.null(object$Rb)) 
            R <- R - object$Rb
        if (!is.null(object$Gb)) 
            G <- G - object$Gb
        if (log) {
            R[R <= 0] <- NA
            G[G <= 0] <- NA
            R <- log(R, 2)
            G <- log(G, 2)
        }
    }
    if (is.null(arrays) & is.null(singlechannels)) {
        arrays <- 1:(ncol(R))
        x <- cbind(R, G)
        if (is.null(groups)) {
            groups <- c(length(arrays), length(arrays))
            if (is.null(col)) 
                cols <- rep(c("red", "green"), groups)
            if (!is.null(col)) {
                if (length(col) != 2) {
                  warning("number of groups=2 not equal to number of col")
                  cols <- "black"
                }
                else {
                  cols <- rep(col, groups)
                }
            }
        }
        else {
            if (!is.null(col)) {
                if (length(as.vector(table(groups))) != length(col)) {
                  warning("number of groups not equal to number of col")
                  cols <- col
                }
                else {
                  cols <- col[groups]
                }
            }
            else {
                warning("warning no cols in col specified for the groups")
                cols <- "black"
            }
        }
    }
    else {
        if (!is.null(singlechannels)) {
            if (!is.null(arrays)) 
                warning("cannot index using arrays AND singlechannels")
            x <- cbind(R, G)[, singlechannels]
            if (is.null(groups)) {
                groups <- c(length(intersect((1:ncol(R)), singlechannels)), 
                  length(intersect(((ncol(R) + 1):ncol(cbind(G, 
                    R))), singlechannels)))
                if (is.null(col)) 
                  cols <- rep(c("red", "green"), groups)
                if (!is.null(col)) {
                  if (length(col) != 2) {
                    warning("number of groups=2 not equal to number of col")
                    cols <- "black"
                  }
                  else {
                    cols <- rep(col, groups)
                  }
                }
            }
            else {
                if (!is.null(col)) {
                  if (length(as.vector(table(groups))) != length(col)) {
                    warning("number of groups not equal to number of col")
                    cols <- col
                  }
                  else {
                    cols <- col[groups]
                  }
                }
                else {
                  print("warning no cols in col specified for the groups")
                  cols <- "black"
                }
            }
        }
        else {
            if (!is.null(arrays)) {
                if (!is.null(singlechannels)) 
                  warning("cannot index using arrays AND singlechannels")
                x <- cbind(R[, arrays], G[, arrays])
                if (is.null(groups)) {
                  groups <- c(length(arrays), length(arrays))
                  if (is.null(col)) 
                    cols <- rep(c("red", "green"), groups)
                  if (!is.null(col)) {
                    if (length(col) != 2) {
                      warning("number of groups=2 not equal to number of col")
                      cols <- "black"
                    }
                    else {
                      cols <- rep(col, groups)
                    }
                  }
                }
                else {
                  if (!is.null(col)) {
                    if (length(as.vector(table(groups))) != length(col)) {
                      warning("number of groups not equal to number of col")
                      cols <- "black"
                    }
                    else {
                      cols <- col[groups]
                    }
                  }
                  else {
                    warning("warning no cols in col specified for the groups")
                    cols <- "black"
                  }
                }
            }
        }
    }
    dens.x <- matDensities(x)
    matplot(dens.x$X, dens.x$Y, xlab = "Intensity", ylab = "Density", 
        main = title, type = "l", col = cols, lwd = 2, 
        lty = 1)
}
##########################################################################
## AGILENT P_VALUE CALCULATION (used by )
##########################################################################

#####################################
## RGLIST SORTING FUNCTION
#####################################
sortRG<-function(x) 
{

#cat("Sorting...")
    ind=grep("chr",x$genes$SystematicName);
#print("sorting : a")
    # Selecting genes (also for non-standard columns)
    res=x[ind,];
#print("sorting : b")
    geneNameTab=sapply(x$genes$SystematicName[ind],strsplit,":");
#print("sorting : c")
    chr=sapply(geneNameTab,"[[",1);
#print("sorting : d")
    region=sapply(geneNameTab,"[[",2);
#print("sorting : e")

    regionTab=sapply(region,strsplit,"-");
#print("sorting : f")

    regionBegin=as.numeric(sapply(regionTab,"[[",1));
#print("sorting : g")

    ordering=order(chr,regionBegin);

#print("sorted RGList")

    return(res[ordering,]);
}
#####################################



#####################################
## f ITERATIVE FINDING FUNCTION
#####################################
f.find<-function(x)
{
	A=log(sqrt(x$R*x$G)); # Intensity

	lIND = which(A<=quantile(A)[2]) # Index of intensity values behind lowest quartile
	hIND = which(A>=quantile(A)[4]) # Index of intensity values over highest quartile

	minVarDif=Inf;
	f_minimizing=0;

	for(f in seq(0.5,1.0,0.01))
	{
		Xlow  = (x$R[lIND]-x$G[lIND])/sqrt( ((f^2) * ((x$R[lIND]^2)+(x$G[lIND]^2))) + (((x$genes$rBGSDUsed[lIND])^2)+((x$genes$gBGSDUsed[lIND])^2)) );
		Xhigh = (x$R[hIND]-x$G[hIND])/sqrt( ((f^2) * ((x$R[hIND]^2)+(x$G[hIND]^2))) + (((x$genes$rBGSDUsed[hIND])^2)+((x$genes$gBGSDUsed[hIND])^2)) );

		underZeroLowVar  = var(Xlow[which(Xlow<0)]);
		underZeroHighVar = var(Xhigh[which(Xhigh<0)]);

		varDif = abs(underZeroLowVar - underZeroHighVar);

		#print(varDif);

		if(varDif < minVarDif)
		{
			minVarDif=varDif;
			f_minimizing=f;
		}
	}
	return(f_minimizing);
}

#####################################



#####################################
# AGILENT function to compute p-values and define region of significant enrichment
#####################################
agilentPValOneRep <- function(chip, thresh_pVal=0.001, thresh_pValNeighb=0.1, thresh_pValTwoOfThree=0.005)
{

	if(class(chip)=="MAList")
	{
		chip=RG.MA(chip);
	}
	else if(class(chip)!="RGList")
	{
		stop("Input type must be either a RGList or a MAlist...");
	}

	if(is.null(chip$genes$rBGSDUsed) | is.null(chip$genes$gBGSDUsed))
	{
		stop("Input argument must provide background standard deviation columns in annotation (YOUR_CHIP$genes$rBGSDUsed and YOUR_CHIP$genes$gBGSDUsed)...");
	}
#print("selecting NA rows")
	naIND=is.na(chip$R) | is.na(chip$G)
#print("removing NA rows")
	chip=chip[!naIND,];
	# background standard deviations are stored into annotation so it's not considered as non-standard column and there is no need to process it separately
#print("NA removed, launching sorting")
	# Sorting the probes by chromosome and coordinates
	sChip=sortRG(chip);

#print("a");
	
	# Estimating f and then computing X values for each probe
	f=f.find(sChip);

#print("b");	

	X=(sChip$R-sChip$G)/sqrt( ((f^2) * ((sChip$R^2)+(sChip$G^2))) + (((sChip$genes$rBGSDUsed)^2)+((sChip$genes$gBGSDUsed)^2)) );
	
	# Computing Xneighb for each probe based on X and X of neighboring probes
	
	minusOneX = vector(length=length(X));
	plusOneX  = vector(length=length(X));
	
	minusOneX=X[2:length(X)];
	minusOneX[length(X)]=0;
	
	plusOneX[2:length(X)]=X[1:(length(X)-1)];
	plusOneX[1]=0;
	
	temp=matrix(ncol=length(X),nrow=3);
	temp[1,]=X;
	temp[2,]=minusOneX;
	temp[3,]=plusOneX;
	
	Xneighb=apply(temp,2,mean);
	Xneighb[1]=0;
	Xneighb[length(X)]=0;
	
	rm(minusOneX);
	rm(plusOneX);
	rm(temp);

	invisible(gc());

#print("c");

	# Computing P(X) and P(Xneighb) for each value assuming gaussian distribution

	X.M  = mean(X);
	X.SD = sd(X);
	P.X=1-pnorm(X, mean=X.M, sd=X.SD);

	Xneighb.M  = mean(Xneighb);
	Xneighb.SD = sd(Xneighb);
	P.Xneighb=1-pnorm(Xneighb, mean=Xneighb.M, sd=Xneighb.SD);

	# Testing for binding events based on P-values

	enrich=vector(mode="logical", length=length(X));
	enrich[1]=FALSE;
	enrich[length(X)]=FALSE;

#print("d");

	for(i in 2:(length(P.X)-1))
	{
		enrich[i]=FALSE;
	
		if(P.Xneighb[i]<thresh_pVal)
		{
			if(P.X[i]<thresh_pVal)
			{
				if( (P.X[i-1]<thresh_pValNeighb) | (P.X[i+1]<thresh_pValNeighb) )
				{
					enrich[i]=TRUE;
				}
			}
			else if(sum(c(P.X[i-1]<thresh_pValTwoOfThree,P.X[i]<thresh_pValTwoOfThree,P.X[i+1]<thresh_pValTwoOfThree))>=2)
			{
				enrich[i]=TRUE;
			}
		}
	}

#print("e");

	#print(P.X);

	sChip$genes$pVal <- P.X;
	sChip$genes$pValNeighb <- P.Xneighb;
	sChip$genes$significantBindingEvent <- enrich;
	sChip$genes$X <- X;

#print("f");

	return(sChip);
}
#####################################



agilentPVal <- function(chip, thresh_pVal=0.001, thresh_pValNeighb=0.1, thresh_pValTwoOfThree=0.005)
{

	if(ncol(chip)>1)
	{
		pVal = matrix();
		pValNeighb = matrix();
		significantBindingEvent = matrix();

#print("A");

		cat("\n");

		res=list();
		for(i in 1:ncol(chip))
		{
#print("AA");
			cat(paste("Computing individual p-values for replicate : ",i,sep=""),"\n");
#print("AB");
			chipTemp=chip[,i];
#print("AC");
			chipTemp$genes$rBGSDUsed=chipTemp$genes$rBGSDUsed[,i];
#print("AD");
			chipTemp$genes$gBGSDUsed=chipTemp$genes$gBGSDUsed[,i];

#print("B");	

			res[[i]]=agilentPValOneRep(chipTemp,thresh_pVal,thresh_pValNeighb,thresh_pValTwoOfThree);

			if(i!=1)
			{
				commonProbes=intersect(res[[i]]$genes$FeatureNum,commonProbes)
			}
			else
			{
				commonProbes=res[[i]]$genes$FeatureNum;
			}
#print("C");
		}
	
		cat(paste("\nbinding P-Values for replicate : ",1,sep=""),"\n");
		chipResMA=res[[1]][which(res[[1]]$genes$FeatureNum %in% commonProbes),];

		for(i in 2:ncol(chip))
		{
			cat(paste("binding P-Values for replicate : ",i,sep=""));
			chipResMA=cbind(chipResMA,res[[i]][which(res[[i]]$genes$FeatureNum %in% commonProbes),])

			chipResMA$genes$rBGSDUsed=NULL;
			chipResMA$genes$gBGSDUsed=NULL;

			chipResMA$genes$pVal <- cbind(chipResMA$genes$pVal,res[[i]][which(res[[i]]$genes$FeatureNum %in% commonProbes),]$genes$pVal)
			chipResMA$genes$pValNeighb <- cbind(chipResMA$genes$pValNeighb,res[[i]][which(res[[i]]$genes$FeatureNum %in% commonProbes),]$genes$pValNeighb)
			chipResMA$genes$significantBindingEvent <- cbind(chipResMA$genes$significantBindingEvent ,res[[i]][which(res[[i]]$genes$FeatureNum %in% commonProbes),]$genes$significantBindingEvent )
			chipResMA$genes$X <- cbind(chipResMA$genes$X,res[[i]][which(res[[i]]$genes$FeatureNum %in% commonProbes),]$genes$X)

		}
		
		
		return(MA.RG(chipResMA,bc.method="none"));
	}
	else
	{
		cat("computing p values")
		return(MA.RG(agilentPValOneRep(chip,thresh_pVal,thresh_pValNeighb,thresh_pValTwoOfThree),bc.method="none"));
	}
}



########################################################################################################################
# 						M A I N   
# 
#	input_files <- list()
#	input_files[[i]] <- list(swap = c("a vector of swap files names"), noswap = c("a vector of swap files names"))
#   i = number of slides
#   length(swap) = number of swap replicates / length(noswap) = number of no swap replicates
#   possible normalisation intra-array : median, loess, peng or vsn
#   possible background substraction : subtract, none, half, minimum, movingmin, edwards, normexp or rma
#   possible normalisation inter-array : median, quantile or none
########################################################################################################################

main <- function(input_files , directory = "test", normalizationIntra = "median", bc.method = "subtract" , normalizationInter="none", mergeSlides=FALSE, mergeReplicates.method="mean", title ="chip-chip")
{
	cat("Start Analysis...\n\n")
	chipRG <- readFE(input_files)
	protocol(chipRG, directory = directory, normalizationIntra = normalizationIntra, bc.method= bc.method, normalizationInter = normalizationInter, title = title, mergeSlides = mergeSlides, mergeReplicates.method=mergeReplicates.method)
rm(list=ls());
}




	
