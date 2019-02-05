  UAM IR team at SIGIR 2018
  ------------------------

  This project contains the code needed to reproduce the experiments of the paper: 
  
> R. Cañamares, [P. Castells](http://ir.ii.uam.es/castells/). [Should I Follow the Crowd? A Probabilistic Analysis of the Effectiveness of Popularity in Recommender Systems](http://ir.ii.uam.es/pubs/sigir2018.pdf). 41st Annual International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2018). Ann Arbor, Michigan, USA, July 2018, pp. 415-424

The software produces the results displayed in figures 3, 5 and 6 in the paper.
  
  Description
  -----------
  
  This code contains two main modules:
- **Module 1:** MonteCarlo computation of the integral described in section 5.3 of the paper, producing the results displayed in Figure 3. 
  
  The module includes the following packages:
    - `es.uam.ir.distribution`: classes to represent values of key conditional probabilities for simulated items.
    - `es.uam.ir.integration`: primary method to numerically integrate the expected precision by Monte Carlo.
    - `es.uam.ir.integration.metric`: classes that sample values for the key probabilities for simulated items and compute the expected value of P@1 (observed and true) for given recommenders.
    - `es.uam.ir.recommender`: classes that rank simulated items according to the probabilistic version of the ranking function of different non-personalized recommendation criteria (random, popularity, average rating and optimal oracle rankings).
- **Module 2:** Computation of the metrics P@1 and nDCG@10 (true and observed versions) as reported in sections 6.2 and 6.3 of the paper. For section 6.2, randomized versions of the crowdsourced dataset ([CM100k](http://ir.ii.uam.es/cm100k)) are generated recreating different independence assumptions, on which non-personalized recommenders are compared: random, popularity, average rating and the optimal rankings, producing the results displayed in Figure 5 (along with basic results for MovieLens 1M). For section 6.3, normalized and non-normalized kNN variants are run on the MovieLens 1M and CM100k datasets. 
  
  The module includes the following packages:
    - `es.uam.ir.crossvalidation`: classes to reproduce a cross-validation split.
    - `es.uam.ir.datagenerator`: classes to manipulate the data and generate the different scenarios of section 6.2.
  
    The module uses the [RankSys](http://ranksys.org/) library, and extends some of its classes. Our extensions are located in the following packages:
  	- `es.uam.ir.ranksys.fast.preference`: extension of RankSys user preference data structures to support the concatenation of several files containing user ratings (the training folds of the cross-validation).
	- `es.uam.ir.ranksys.nn.user`: extension of RankSys implementations of kNN collaborative filtering, adding normalized user-based variants.
	- `es.uam.ir.ranksys.rec.fast.basic`: extension of RankSys implementations of non-personalized recommendation, adding the implementation of average rating, relevant popularity and the optimal rankings.

The code contains two more independent packages:
  - `es.uam.ir.sigir2018`: this package contains the specific top-level classes to generate each figure (`Figure3.java`, `Figure5.java` and `Figure6.java`) and a `Main` class calling all three of them.
  - `es.uam.ir.util`: just includes a timing class. 
  
  
  System Requirements
  -------------------

  - Java JDK:
    1.8 or above (the software was tested using the version 1.8.0_181).

  - Maven:
    Tested with version 3.6.0.

	
  Installation
  ------------
  
  Download all the files and unzip them into any root folder.
  
  From the root folder run the command: 
  
    mvn package
    
  
  Execution
  ---------
  
  To run all the experiments run the command:
  
    java -cp .\target\SIGIR2018-0.1-jar-with-dependencies.jar es.uam.ir.sigir2018.Main
  
  A file `results.txt` will be generated inside the root folder. 
  
  A MS Excel file `figures.xlsx` is provided, ready for the results in the above file to be pasted where indicated. Upon doing this, similar graphs to the ones in the paper will be displayed. 
    
  Example of the output file
  ---------------------------
  
  Exact metric values change slightly from one execution to another:
  
  
	------------------ Figure 3 ------------------

		Random	Popularity	Average rating	Optimal
	Observed P@1	0.03116459901130887	0.5578455975753752	0.11013380638108188	0.5627394708115975
	True P@1	0.5016332961450131	0.7107214036246516	0.8584746659065928	0.999725156715827

	------------------ Figure 5 ------------------

	MovieLens 1M

	Recommender	Observed P@1	Observed nDCG@10
	Random	0.005596026490066213	0.0038458865171088147
	Popularity	0.2098675496688449	0.1493438938396316
	Average	0.14589403973508905	0.10196538518818263
	f^ : Optimal observed ranking	0.21079470198672542	0.14903111060720312

	Crowdsourced 100k dataset

	a) All ratings
	Recommender	Observed P@1	Observed nDCG@10
	Random	0.005597722960151804	0.006625197873998685
	Popularity	0.01605313092979125	0.023148557509490155
	Average	0.015559772296015156	0.0237944908417082
	f^ : Optimal observed ranking	0.020322580645161254	0.02796306084610136

	b) Actual discovery (mixed dependency)
	Recommender	Observed P@1	True P@1	Observed nDCG@10	True nDCG@10
	Random	0.0012623158437221592	0.02125768082996259	0.0030406392598662816	0.014701510258760672
	Popularity	0.015321994598499788	0.01729393021347722	0.03982617127699494	0.017804988702735714
	Average	0.014257341499672569	0.02088608860550921	0.02300468271524813	0.02512811339866609
	f^ : Optimal observed ranking	0.0196603409044686	0.02283571137693351	0.04644629667212194	0.02106153676691221
	f : Optimal true ranking	0.003351938420540986	0.05523351470932518	0.005674788588152241	0.041004039017661705

	c) Relevance-independent discovery
	Recommender	Observed P@1	True P@1	Observed nDCG@10	True nDCG@10
	Random	6.451612903225806E-4	0.026299810246679267	0.002227889654839054	0.017209367069424258
	Popularity	0.015294117647058802	0.041347248576850015	0.03329369512325497	0.027787715955213316
	Average	0.0019354838709677417	0.05419354838709682	0.0063892457331493294	0.03778386756462834
	f^ : Optimal observed ranking	0.021973434535104325	0.040702087286527425	0.04410416899205351	0.025582880668314423
	f : Optimal true ranking	0.0010626185958254267	0.08280834914611082	0.006117205206202229	0.06696364258953402

	d) Item-independent discovery
	Recommender	Observed P@1	True P@1	Observed nDCG@10	True nDCG@10
	Random	0.0011026820259398573	0.02046212901060843	0.0033109122402735823	0.014363567021178286
	Popularity	0.0025294986581055673	0.046801001495267315	0.007259395508198876	0.03953130874953142
	Average	0.0025481311644216494	0.04522266377801705	0.006642338070580163	0.03175942219956042
	f^ : Optimal observed ranking	0.007606497819511752	0.04750665111560022	0.01767555923096799	0.03509623106917378
	f : Optimal true ranking	0.0041455827332742765	0.06819027535723052	0.010894364648301045	0.05805301604711277

	------------------ Figure 6 ------------------

	MovieLens 1M

	Recommender	Observed P@1	Observed nDCG@10
	Non-normalized user-based kNN	0.4130132450330405	0.2825003770150513
	Normalized user-based kNN	0.1470529801324397	0.1594671579531564

	Crowdsourced 100k

	Recommender	Observed P@1	True P@1	Observed nDCG@10	True nDCG@10
	Non-normalized user-based kNN	0.012084462563288198	0.01920137092139627	0.027545256200761915	0.01791544253621822
	Normalized user-based kNN	0.006700879456540734	0.023814526504910248	0.017320576589456294	0.01837708823018656

  
		
 
  API documentation
  -----------------
	
  To generate the api documentation run the command: 
  
    mvn javadoc:javadoc
  
  The documentation will be generated in the folder `target/site`.  
