FROM ubuntu:latest
LABEL authors="wayou"

ENTRYPOINT ["top", "-b"]