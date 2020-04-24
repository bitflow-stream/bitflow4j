# bitflowstream/java-build
# docker build -t bitflowstream/java-build -f build.Dockerfile .
FROM maven:3.6-jdk-11

RUN apt-get update && \
    apt-get -y install apt-transport-https ca-certificates curl gnupg2 software-properties-common && \
    curl -fsSL https://download.docker.com/linux/$(. /etc/os-release; echo "$ID")/gpg > /tmp/dkey; apt-key add /tmp/dkey && \
    add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/$(. /etc/os-release; echo "$ID") $(lsb_release -cs) stable" && \
    apt-get update && \
    apt-get -y install docker-ce qemu-user mercurial git jq

# Enable docker-cli experimental features
RUN mkdir ~/.docker && echo '{\n\t"experimental": "enabled"\n}' > ~/.docker/config.json
