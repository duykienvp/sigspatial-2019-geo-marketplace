# This document includes some notes for installing on macOS. Tested on macos 10.14 and 10.15
We use homebrew to install dependencies.

    brew install gmp

    brew install libomp

We need to make sure that openssl is visible for other crypto library, including Charm.

    brew install openssl
    cd /usr/local/include 
    ln -s ../opt/openssl/include/openssl .

Sometimes we may need install SDK header:
 
    sudo installer -pkg /Library/Developer/CommandLineTools/Packages/macOS_SDK_headers_for_macOS_10.14.pkg -target /

Also, install charm and flint instead of using old dependency from pip