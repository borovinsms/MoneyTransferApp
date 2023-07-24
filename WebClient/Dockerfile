FROM ubuntu

EXPOSE 3000

RUN apt update -y

RUN apt install nodejs -y

RUN apt install npm -y

COPY card-transfer-master /

RUN npm i

CMD ["npm", "run", "start"]