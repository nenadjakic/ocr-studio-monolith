FROM eclipse-temurin:21-jdk AS app_stage

WORKDIR /app

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .
COPY gradle/ ./gradle

RUN ./gradlew clean

COPY src/ ./src/

RUN ./gradlew clean bootJar


FROM ubuntu:22.04 AS tesseract

LABEL authors="nenadjakic"

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get -y update
RUN apt-get -y install \
    automake \
    ca-certificates \
    g++ \
    git \
    libtool \
    libleptonica-dev \
    make \
    pkg-config

RUN apt-get -y install --no-install-recommends \
    asciidoc \
    docbook-xsl \
    xsltproc

WORKDIR /src

ARG TESSERACT_VERSION

RUN git clone -b $TESSERACT_VERSION https://github.com/tesseract-ocr/tesseract.git

WORKDIR /src/tesseract

RUN ./autogen.sh
RUN ./configure
RUN make
RUN make install
RUN ldconfig

RUN apt-get -y install \
    wget

WORKDIR /usr/local/share/tessdata/

COPY .tesseract/get-languages.sh .
COPY .tesseract/languages.txt .

RUN chmod +x ./get-languages.sh
RUN ./get-languages.sh

WORKDIR /usr/local/share/tessdata/scripts

COPY .tesseract/get-scripts.sh .
COPY .tesseract/scripts.txt .

RUN chmod +x ./get-scripts.sh
RUN ./get-scripts.sh

RUN apt-get -y install openjdk-21-jdk

WORKDIR /app

COPY --from=app_stage /app/build/libs/ocr-studio.jar .

CMD ["java", "-jar", "ocr-studio.jar"]
