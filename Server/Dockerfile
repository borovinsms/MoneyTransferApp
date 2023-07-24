FROM openjdk

ADD target/Server-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 5500

CMD ["mkdir", "/files"]

ENV PATH_CARDS_FILE="/files/cards.json"

ENV PATH_TRANSACTION_FILE="/files/transactions.json"

ENV PATH_LOG_FILE="/files/log.txt"

ENV ORIGIN_HOSTS="https://serp-ya.github.io, http://localhost:3000"

ENV VERIFICATION_CODE="0000"

ENV TRANSFER_FEE="1"

ENV CARD_SIZE="16"

ENV CVV_SIZE="3"

ENV DATE_VALID_TILL_SIZE="5"

ENTRYPOINT ["java", "-jar", "app.jar"]

VOLUME ./repo /files

ADD src/main/resources/files/cards.json /files/cards.json