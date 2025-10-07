package com.example.ad_service.model;

import java.util.Objects;


public class Ad {
    private String redirectUrl;
    private String text;

    public Ad() {
    }

    public Ad(String redirectUrl, String text) {
        this.redirectUrl = redirectUrl;
        this.text = text;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return Objects.equals(redirectUrl, ad.redirectUrl) &&
               Objects.equals(text, ad.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(redirectUrl, text);
    }

    @Override
    public String toString() {
        return "Ad{" +
               "redirectUrl='" + redirectUrl + '\'' +
               ", text='" + text + '\'' +
               '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String redirectUrl;
        private String text;

        public Builder redirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Ad build() {
            return new Ad(redirectUrl, text);
        }
    }
}