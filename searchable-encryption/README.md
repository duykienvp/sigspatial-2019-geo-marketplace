# A Privacy-Preserving, Accountable and Spam-Resilient Geo-Marketplace

This project implements searchable encryption schemes for the paper. 

This project aims to implement several searchable encryption schemes with support for range query (mainly 2-D).

Internally, it uses:
  * [Charm] as the main crypto-prototype library
  * [cryptography] as the cryptographic library
  * [pybloomfiltermmap3] as the bloomfilter library
  * [bitarray] and [mmh3] for SHVE implementation
 
Authors:
 * Kien Nguyen, University of Southern California

Contact Kien Nguyen for questions about the code:
  kien.nguyen@usc.edu
  
## Structure ##

This library ships with the following main modules:
 * **Symmetric searchable encryption**: using Hidde-Cross Tags (HXT), in [hxt.py], based on the paper [Lai '18]. 
 * **Asymmetric searchable encryption**: using Hidden Vector Encryption (HVE), in [hve.py], based on Appendix A of [Ghinita '14].
  
Folder structure is as follows:
 * `benchmark`: benchmark output folder
 * `datasets`: data folder
 * `notebook`: a notebook for plottinh experiment results
 * `searchableencryption`: main source folder
 * `tests`: test and benchmark script
   
## Prerequisites ##

Make sure you have the following installed:
 * [Python 3](https://www.python.org/)
 * [GMP 6.x](http://gmplib.org/)
 * [PBC](http://crypto.stanford.edu/pbc/download.html)
 * [OpenSSL](http://www.openssl.org/source/)
 * [Charm]
 * [Flint](http://www.flintlib.org/)

## Installation ##
    
See the installation for Ubuntu 18.04 in [Dockerfile].
For macOS, there are some more notes in [install-macos-notes].

Make sure a ```logs``` folder is created before running.

## Usage ##

Use [config.yml] to configure the parameters. Most of parameters are explained in the yml file.

Use [logging.yml] to configure the logging parameters.

Use the [taskexecutor.py] script to run program. This can be the entry to the entire program.
The script takes the following arguments:
  * `--task`: the task to execute.  
    Tasks are: 
    * `gen`: generate data
    * `benchmark`: for benchmarking
    * `test`: testing a specific functionality
    
  * `--scheme`: the scheme to execute.  
    * `hve`: Hidden Vector Encryption (i.e., asymmetric Searchable Encryption)
    * `hxt`: Hidden Cross Tags (i.e., symmetric Searchable Encryption)
    * `select_checkins_gowalla`: select check-ins from [Gowalla Dataset]
    * `convert_checkins_to_doc`: convert check-ins to documents
    * `gen_queries_gowalla_hve`: generate queries from [Gowalla Dataset] for HVE
    * `convert_queries_gowalla_hve_to_hxt`: convert queries for HVE to queries for HXT
    * `generate_hxt_index`: generate HXT index
  
### Typical run steps ###
Before running each of these steps, make sure you have correct configuration.

- Select checkins Gowalla: select check-ins from gowalla file: note that in this repository, 
we already did this step and got 4 check-in files in [checkins]. 

    ```
    python3 taskexecutor.py --task gen --scheme select_checkins_gowalla --input-file datasets/loc-gowalla_totalCheckins.txt
    ```
    
    This step outputs to `checkins_dir` in `config.yml`.
    
- Convert checkins to documents: convert check-ins to documents

    ```
    python3 taskexecutor.py --task gen --scheme convert_checkins_to_doc
    ```
  
  This step outputs to `documents_dir` under `data_files` in `config.yml`.

- Generate query HVE: generate queries for HVE

    ```
    python3 taskexecutor.py --task gen --scheme gen_queries_gowalla_hve --input-file datasets/gowalla_LA/checkins/gowalla_LA_checkins_10000.txt
    ```
  This step outputs to the current directory. We then should me the generated query files to a query folder, e.g. `datasets/queries/hve/`
    
- Convert query HVE to query HXT: convert queries for HVE to queries for HXT. We just want to make sure that these are queries for the same areas.
    Make sure we pointed the query directory in `config.yml` to HVE query directory.

    ```
    python3 taskexecutor.py --task gen --scheme convert_queries_gowalla_hve_to_hxt
    ```
  
  This step outputs to the current directory. We then should me the generated query files to a query folder, e.g. `datasets/queries/hxt/`

- Generate HXT index: generate index for HXT

    ```
    python3 taskexecutor.py --task benchmark --scheme generate_hxt_index
    ```
  
  This step outputs index to `dataset_dir` under `index` in `config.yml`, and also write to benchmark output file.
    
- Benchmark HXT scheme:

    ```
    python3 taskexecutor.py --task benchmark --scheme hxt
    ```
  
  This step writes to benchmark output file.
  
- Benchmark HVE scheme:

    ```
    python3 taskexecutor.py --task benchmark --scheme hve
    ```
  
  This step writes to benchmark output file.
  
## Some issues of Charm crypto library ##
- Open `FILE` without closing. Might cause our segment fault. See `init_pbc_param` function in `pairingmodule.c` (line 328)
- `pairing` hash error. Example
```python
ID1 = "This is an id"
k = group.hash(ID1,G1)
```

[Charm]: http://charm-crypto.io/
[cryptography]: https://cryptography.io
[pybloomfiltermmap3]: https://pypi.org/project/pybloomfiltermmap3/
[bitarray]: https://pypi.org/project/bitarray/
[mmh3]: https://pypi.org/project/mmh3/ 

[taskexecutor.py]: /taskexecutor.py
[install-macos-notes]: /install-macos-note.md    
[hve]: /searchableencryption/hve
[hve.py]: /searchableencryption/hve/hve.py
[hxt.py]: /searchableencryption/sse/hxt.py

[Ghinita '14]: https://dl.acm.org/citation.cfm?id=2557559
[Lai '18]: https://dl.acm.org/citation.cfm?id=3243753

[Dockerfile]: /Dockerfile
[config.yml]: /config.yml
[logging.yml]: /logging.yml
[checkins]: /datasets/gowalla_LA/checkins

[Gowalla Dataset]: https://snap.stanford.edu/data/loc-gowalla.html
