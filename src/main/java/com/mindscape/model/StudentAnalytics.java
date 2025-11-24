package com.mindscape.activityprovider.model;

public class StudentAnalytics {

    private final String activityId;
    private final String studentId;

    // Parâmetros de configuração
    private String prompt;
    private Integer maxIdeas;

    // Métricas
    private int ideasGenerated;
    private long firstAccessTimestamp;
    private long lastInteractionTimestamp;
    private boolean reflectionSubmitted;

    // Conteúdo textual (qualitativo)
    private String ideasText;

    public StudentAnalytics(String activityId, String studentId) {
        this.activityId = activityId;
        this.studentId = studentId;
        this.firstAccessTimestamp = System.currentTimeMillis();
        this.lastInteractionTimestamp = this.firstAccessTimestamp;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Integer getMaxIdeas() {
        return maxIdeas;
    }

    public void setMaxIdeas(Integer maxIdeas) {
        this.maxIdeas = maxIdeas;
    }

    public int getIdeasGenerated() {
        return ideasGenerated;
    }

    public void incrementIdeasGenerated(int amount) {
        this.ideasGenerated += amount;
        this.lastInteractionTimestamp = System.currentTimeMillis();
    }

    public long getFirstAccessTimestamp() {
        return firstAccessTimestamp;
    }

    public long getLastInteractionTimestamp() {
        return lastInteractionTimestamp;
    }

    public void touch() {
        this.lastInteractionTimestamp = System.currentTimeMillis();
    }

    public boolean isReflectionSubmitted() {
        return reflectionSubmitted;
    }

    public void setReflectionSubmitted(boolean reflectionSubmitted) {
        this.reflectionSubmitted = reflectionSubmitted;
        this.lastInteractionTimestamp = System.currentTimeMillis();
    }

    public String getIdeasText() {
        return ideasText;
    }

    public void appendIdeasText(String text) {
        if (text == null || text.isBlank()) return;
        if (this.ideasText == null || this.ideasText.isBlank()) {
            this.ideasText = text.trim();
        } else {
            this.ideasText = this.ideasText + "\n\n---\n\n" + text.trim();
        }
        this.lastInteractionTimestamp = System.currentTimeMillis();
    }

    public long getTimeSpentSeconds() {
        return (lastInteractionTimestamp - firstAccessTimestamp) / 1000L;
    }
}
