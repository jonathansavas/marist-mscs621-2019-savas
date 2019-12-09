FROM python:3.7-alpine

RUN pip install --upgrade pip

RUN mkdir /opt/app
RUN mkdir /opt/app/static

ADD webpage-rest-server.py /opt/app
ADD requirements.txt /opt/app
ADD /static /opt/app/static

RUN pip install -r /opt/app/requirements.txt

WORKDIR /opt/app
EXPOSE 7777

CMD python webpage-rest-server.py