FROM ubuntu:18.04

# Install necesarry dependencies
RUN apt-get -yqq update
RUN apt-get -yqq install software-properties-common gcc autoconf libssl1.0-dev libtool make python3 python3-dev python3-setuptools python3-pip flex bison libgmp-dev libflint-dev

# Upgrade pip and setup tools
RUN pip3 install pip --upgrade
RUN pip3 install setuptools --upgrade

# Install PBC
RUN add-apt-repository -y ppa:duykienvp/crypto-ppa
RUN apt-get -yqq update
RUN apt-get -yqq install libpbc-dev

# Copy project content
ADD . / searchable-encryption/
WORKDIR /searchable-encryption/

# Install python dependencies
RUN pip3 install cython
RUN pip3 install -r requirements.txt

# Test
RUN pip3 install pytest
RUN pytest
