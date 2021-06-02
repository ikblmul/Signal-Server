FROM ubuntu:20.04


ENV TZ=Asia/Dubai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get update \
    && apt-get install -y gnupg gosu curl libssl-dev ca-certificates zip unzip git supervisor libcap2-bin vim default-jre maven \
    # loggin java and maven version
    && java -version \ 
    && mvn --version \
    && mkdir -p ~/.gnupg \
    # && echo "disable-ipv6" >> ~/.gnupg/dirmngr.conf \
    # && echo "deb http://ppa.launchpad.net/ondrej/php/ubuntu focal main" > /etc/apt/sources.list.d/ppa_ondrej_php.list \
    && apt-get update \
    && apt-get -y autoremove \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

COPY . /var/www

WORKDIR /var/www

RUN mvn dependency:go-offline

CMD [ "java","-jar","service/target/TextSecureServer-5.88.jar", "server" , "service/config/config.yml" ]

EXPOSE 8000
