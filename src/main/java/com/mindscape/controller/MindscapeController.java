package com.mindscape.activityprovider.controller;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.dto.AnalyticsResponseItem;
import com.mindscape.activityprovider.model.StudentAnalytics;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MindscapeController {

    // activityId -> (studentId -> analytics)
    private final Map<String, Map<String, StudentAnalytics>> analyticsStore = new ConcurrentHashMap<>();

    // --------- Endpoints básicos / healthcheck ---------

    @GetMapping("/")
    public String home() {
        return "MindScape Activity Provider is running.";
    }

    // --------- 1) Página de configuração (config_url) ---------

    // Ex: https://.../configuracao-mindscape.html
    @GetMapping(value = "/configuracao-mindscape.html", produces = MediaType.TEXT_HTML_VALUE)
    public String configPage() {
        // Campos "prompt" e "maxIdeas" são referidos em json_params_url
        return """
                <!DOCTYPE html>
                <html lang="pt">
                <head>
                    <meta charset="UTF-8">
                    <title>Configuração MindScape Activity</title>
                </head>
                <body>
                    <h1>Configuração da atividade MindScape</h1>
                    <form>
                        <label for="prompt">Prompt base para o raciocínio criativo:</label><br/>
                        <textarea id="prompt" name="prompt" rows="4" cols="60"
                                  placeholder="Ex.: Imagine um cenário futuro em que..."></textarea>
                        <br/><br/>
                        <label for="maxIdeas">Número máximo de ideias sugeridas ao estudante:</label><br/>
                        <input type="number" id="maxIdeas" name="maxIdeas" min="1" max="50" value="10"/>
                    </form>
                    <p>Esta página é embutida pela Inven!RA para configurar a atividade.</p>
                </body>
                </html>
                """;
    }

    // --------- 2) Lista de parâmetros de configuração (json_params_url) ---------

    // Ex: https://.../json-params-mindscape
    @GetMapping("/json-params-mindscape")
    public List<Map<String, String>> jsonParams() {
        List<Map<String, String>> params = new ArrayList<>();

        Map<String, String> prompt = new HashMap<>();
        prompt.put("name", "prompt");
        prompt.put("type", "text/plain");
        params.add(prompt);

        Map<String, String> maxIdeas = new HashMap<>();
        maxIdeas.put("name", "maxIdeas");
        maxIdeas.put("type", "integer");
        params.add(maxIdeas);

        return params;
    }

    // --------- 3) Deploy da atividade (user_url) ---------
    //
    // A Inven!RA chama user_url com o ID da instância para obter um URL base.
    // Por simplicidade:
    // - Recebemos opcionalmente {"activityID": "..."}
    // - Devolvemos uma path relativa que será usada como endpoint para o POST seguinte.

    public static class DeployRequest {
        public String activityID;

        public String getActivityID() {
            return activityID;
        }

        public void setActivityID(String activityID) {
            this.activityID = activityID;
        }
    }

    @PostMapping("/deploy-mindscape")
    public String deployActivity(@RequestBody(required = false) DeployRequest req) {
        String activityId = (req != null && req.activityID != null && !req.activityID.isBlank())
                ? req.activityID
                : UUID.randomUUID().toString();

        // Garante que temos um mapa para esta atividade
        analyticsStore.computeIfAbsent(activityId, k -> new ConcurrentHashMap<>());

        // Devolvemos um URL onde a Inven!RA vai fazer POST com config + Inven!RAstdID:
        // Inven!RA irá depois fazer POST a este URL com:
        // { "activityID": "...", "Inven!RAstdID": "...", "json_params": {...} }
        String path = "/mindscape/start?activityID=" + urlEncode(activityId);
        return path;
    }

    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // --------- 4) Receber config + estudante e devolver URL final do estudante ---------

    public static class StartRequest {
        public String activityID;
        public String InvenRAstdID;  // pode vir com este nome ou com "Inven!RAstdID"
        public String InvenRAStdID;  // fallback
        public Map<String, Object> json_params;

        public String getEffectiveStudentId() {
            if (InvenRAstdID != null && !InvenRAstdID.isBlank()) return InvenRAstdID;
            if (InvenRAStdID != null && !InvenRAStdID.isBlank()) return InvenRAStdID;
            return null;
        }
    }

    @PostMapping("/mindscape/start")
    public String startActivityForStudent(@RequestBody StartRequest req) {
        String activityId = req.activityID;
        String studentId = req.getEffectiveStudentId();

        if (activityId == null || studentId == null) {
            throw new IllegalArgumentException("activityID e Inven!RAstdID são obrigatórios");
        }

        Map<String, StudentAnalytics> perActivity =
                analyticsStore.computeIfAbsent(activityId, k -> new ConcurrentHashMap<>());

        StudentAnalytics sa = perActivity.computeIfAbsent(studentId,
                sid -> new StudentAnalytics(activityId, sid));

        // Guardar parâmetros de configuração recebidos
        if (req.json_params != null) {
            Object promptObj = req.json_params.get("prompt");
            if (promptObj != null) {
                sa.setPrompt(String.valueOf(promptObj));
            }

            Object maxIdeasObj = req.json_params.get("maxIdeas");
            if (maxIdeasObj != null) {
                try {
                    sa.setMaxIdeas(Integer.parseInt(String.valueOf(maxIdeasObj)));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // Devolver URL onde o estudante irá realmente interagir (HTML)
        String url = "/mindscape/run?activityID=" + urlEncode(activityId)
                + "&studentID=" + urlEncode(studentId);
        return url;
    }

    // --------- 5) Página HTML para o estudante interagir ---------

    @GetMapping(value = "/mindscape/run", produces = MediaType.TEXT_HTML_VALUE)
    public String runPage(@RequestParam("activityID") String activityId,
                          @RequestParam("studentID") String studentId) {

        Map<String, StudentAnalytics> perActivity =
                analyticsStore.computeIfAbsent(activityId, k -> new ConcurrentHashMap<>());

        StudentAnalytics sa = perActivity.computeIfAbsent(studentId,
                sid -> new StudentAnalytics(activityId, sid));

        sa.touch();

        String promptText = (sa.getPrompt() != null && !sa.getPrompt().isBlank())
                ? sa.getPrompt()
                : "Use este espaço para gerar ideias criativas sobre o tema em estudo.";

        String maxIdeasHint = (sa.getMaxIdeas() != null)
                ? "(sugestão: " + sa.getMaxIdeas() + " ideias)"
                : "";

        return """
                <!DOCTYPE html>
                <html lang="pt">
                <head>
                    <meta charset="UTF-8">
                    <title>MindScape - Atividade Criativa</title>
                </head>
                <body>
                    <h1>MindScape - Raciocínio Criativo</h1>
                    <p><strong>Activity ID:</strong> %s</p>
                    <p><strong>Student ID:</strong> %s</p>
                    <p><strong>Prompt:</strong> %s</p>
                    <p>%s</p>

                    <form method="post" action="/mindscape/submit">
                        <input type="hidden" name="activityID" value="%s"/>
                        <input type="hidden" name="studentID" value="%s"/>

                        <label for="ideasText">Ideias (uma por linha, se possível):</label><br/>
                        <textarea id="ideasText" name="ideasText" rows="8" cols="80"
                                  placeholder="Escreva aqui as suas ideias..."></textarea>
                        <br/><br/>

                        <label for="reflection">Breve reflexão sobre o que mudou no seu ponto de vista:</label><br/>
                        <textarea id="reflection" name="reflection" rows="4" cols="80"
                                  placeholder="O que aprendeu ou repensou?"></textarea>
                        <br/><br/>

                        <button type="submit">Submeter</button>
                    </form>

                    <p>Esta página é um exemplo simples de interface de atividade; na prática,
                    poderia ser muito mais rica (UX, timers, passos guiados, etc.).</p>
                </body>
                </html>
                """.formatted(
                escapeHtml(activityId),
                escapeHtml(studentId),
                escapeHtml(promptText),
                escapeHtml(maxIdeasHint),
                escapeHtml(activityId),
                escapeHtml(studentId)
        );
    }

    // Submissão das ideias/reflexão (atualiza analytics)
    @PostMapping(value = "/mindscape/submit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView submitActivity(@RequestParam("activityID") String activityId,
                                       @RequestParam("studentID") String studentId,
                                       @RequestParam(name = "ideasText", required = false) String ideasText,
                                       @RequestParam(name = "reflection", required = false) String reflection) {

        Map<String, StudentAnalytics> perActivity =
                analyticsStore.computeIfAbsent(activityId, k -> new ConcurrentHashMap<>());

        StudentAnalytics sa = perActivity.computeIfAbsent(studentId,
                sid -> new StudentAnalytics(activityId, sid));

        if (ideasText != null && !ideasText.isBlank()) {
            sa.appendIdeasText(ideasText);
            // contar linhas não vazias como número de ideias adicionadas
            int newIdeas = (int) Arrays.stream(ideasText.split("\\R"))
                    .filter(l -> !l.isBlank())
                    .count();
            sa.incrementIdeasGenerated(newIdeas);
        }

        if (reflection != null && !reflection.isBlank()) {
            sa.appendIdeasText("\n[Reflexão]\n" + reflection);
            sa.setReflectionSubmitted(true);
        }

        // Redireciona de volta para a página de execução (poderias criar página de "obrigado")
        String redirectUrl = "/mindscape/run?activityID=" + urlEncode(activityId)
                + "&studentID=" + urlEncode(studentId);
        return new RedirectView(redirectUrl);
    }

    // --------- 6) Lista de analytics disponíveis (analytics_list_url) ---------

    @GetMapping("/lista-analytics-mindscape")
    public Map<String, List<Map<String, String>>> analyticsList() {
        Map<String, List<Map<String, String>>> result = new LinkedHashMap<>();

        List<Map<String, String>> qual = new ArrayList<>();
        List<Map<String, String>> quant = new ArrayList<>();

        // Qualitativos
        qual.add(mapOf("name", "Prompt used", "type", "text/plain"));
        qual.add(mapOf("name", "Ideas text", "type", "text/plain"));

        // Quantitativos
        quant.add(mapOf("name", "Ideas generated", "type", "integer"));
        quant.add(mapOf("name", "Time spent (seconds)", "type", "integer"));
        quant.add(mapOf("name", "Reflection submitted", "type", "boolean"));

        result.put("qualAnalytics", qual);
        result.put("quantAnalytics", quant);
        return result;
    }

    // --------- 7) Obtenção dos analytics (analytics_url) ---------

    public static class AnalyticsRequest {
        public String activityID;

        public String getActivityID() {
            return activityID;
        }

        public void setActivityID(String activityID) {
            this.activityID = activityID;
        }
    }

    @PostMapping("/analytics-mindscape")
    public List<AnalyticsResponseItem> getAnalytics(@RequestBody AnalyticsRequest req) {
        String activityId = req.activityID;
        if (activityId == null || activityId.isBlank()) {
            throw new IllegalArgumentException("activityID é obrigatório");
        }

        Map<String, StudentAnalytics> perActivity = analyticsStore.getOrDefault(activityId, Map.of());
        List<AnalyticsResponseItem> response = new ArrayList<>();

        for (StudentAnalytics sa : perActivity.values()) {
            List<AnalyticItem> quant = new ArrayList<>();
            List<AnalyticItem> qual = new ArrayList<>();

            quant.add(new AnalyticItem("Ideas generated", "integer", sa.getIdeasGenerated()));
            quant.add(new AnalyticItem("Time spent (seconds)", "integer", sa.getTimeSpentSeconds()));
            quant.add(new AnalyticItem("Reflection submitted", "boolean", sa.isReflectionSubmitted()));

            qual.add(new AnalyticItem("Prompt used", "text/plain", sa.getPrompt()));
            qual.add(new AnalyticItem("Ideas text", "text/plain", sa.getIdeasText()));

            AnalyticsResponseItem item =
                    new AnalyticsResponseItem(sa.getStudentId(), quant, qual);

            response.add(item);
        }

        return response;
    }

    // --------- Helpers ---------

    private Map<String, String> mapOf(String k1, String v1, String k2, String v2) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
