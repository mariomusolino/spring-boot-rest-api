package com.odissey.tourai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odissey.tourai.dto.TourRequest;
import com.odissey.tourai.dto.TourResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourService {

    private final OpenAiChatModel openAiChatModel;
    private final ObjectMapper objectMapper;

    private static final String TOUR_PROMPT = """
            Crea un tour turistico per {destinazione} della durata di {durata} giorni che parte il giorno {start_date}.
            {tipologia_text}
            {budget_text}
            
            Genera un tour realistico e dettagliato. Includi informazioni sui luoghi da visitare,
            attività consigliate, costi approssimativi e suggerimenti pratici.
            
            La data di inizio del tour deve essere la data di oggi più 15 giorni.
            
            IMPORTANTE: Rispondi ESCLUSIVAMENTE con un JSON valido nel seguente formato:
            {{
                "title": "Nome del tour",
                "description": "Descrizione dettagliata del tour con itinerario giornaliero",
                "startDate": {start_date},
                "endDate": "start_date + la durata (yyyy-MM-dd)",
                "countryCode": "codice ISO 2 della nazione",
                "minPax": "numero minimo di partecipanti"
                "maxPax": "minPax + 10",
                "price": "prezzo basato sul budget"
            }}
            La descrizione dettagliata (ovvero il valore di description) deve essere una stringa unica e NON deve essere strutturato come un json.
            Il prezzo deve essere un numero intero positivo e non deve includere il tipo di valuta o altre considerazioni.
            Non includere altro testo al di fuori del JSON.
            """;



    public TourResponse generateTour(TourRequest request){
        String responseCleaned = "";

        try {
            String tipologiaText = request.getType() != null && !request.getType().isEmpty()
                    ? "Tipologia di tour: " + request.getType() + "."
                    : "";

            String budgetText = request.getBudget() != null && !request.getBudget().isEmpty()
                    ? "Budget disponibile: " + request.getBudget() + "."
                    : "";


            PromptTemplate promptTemplate = new PromptTemplate(TOUR_PROMPT);
            Prompt prompt = promptTemplate.create(Map.of(
                    "destinazione", request.getDestinazione(),
                    "durata", String.valueOf(request.getDurata()),
                    "tipologia_text", tipologiaText,
                    "budget_text", budgetText,
                    "start_date", LocalDate.now().plusDays(15L)
            ));
            String response = openAiChatModel.call(prompt).getResult().getOutput().getText();

            if(response != null)
                responseCleaned = StringUtils.replaceEach(
                        response,
                        new String[] {"```", "json", "\t", "\r"},
                        new String[] {"",    "",     "",   "",}
                ).replaceFirst("\n", "");

            TourResponse tourResponse = objectMapper.readValue(responseCleaned, TourResponse.class);
            return tourResponse;

        } catch (Exception e) {
            log.error("Errore nella generazione del tour: {}", e.getMessage());
        }

        return null;
    }

}

