FROM debian:latest
LABEL maintainer="gabriel@gaucimaistre.com"

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ENV PATH /opt/conda/bin:$PATH

ARG BUILD_DB

RUN apt-get update --fix-missing && \
    apt-get install -y wget build-essential sqlite3 libsqlite3-dev git ssmtp bash openssl && \
    wget -q https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh -O ~/miniconda.sh && \
    bash ~/miniconda.sh -b -p /opt/conda && \
    rm ~/miniconda.sh && \
    ln -s /opt/conda/etc/profile.d/conda.sh /etc/profile.d/conda.sh && \
    echo ". /opt/conda/etc/profile.d/conda.sh" >> ~/.bashrc && \
    echo "conda activate base" >> ~/.bashrc && \
    apt-get clean

COPY . /app
WORKDIR /app

RUN conda update conda && \
    conda env create --file environment.yml && \
    conda clean --yes --tarballs --packages

ENV PATH /opt/conda/envs/env_gatekeeping/bin:$PATH

EXPOSE 5000 587

RUN python -c "import os; print('SECRET_KEY =', os.urandom(16))" >> gatekeeping/config.py && \
    pip install . && \
    export FLASK_APP="gatekeeping" && \
    bash script.sh

CMD [ "gunicorn", "-w 4", "-b 0.0.0.0:5000", "gatekeeping:create_app()" ]