module org.randomcoder.website {
    // java modules
    requires java.base;
    requires java.desktop;
    requires java.sql;
    requires jdk.unsupported;

    // third-party modules
    requires ch.qos.logback.classic;
    requires commons.dbcp2;
    requires commons.httpclient;
    requires com.fasterxml.classmate;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.module.jakarta.xmlbind;
    requires jakarta.annotation;
    requires jakarta.el;
    requires jakarta.inject;
    requires jakarta.xml.bind;
    requires jakarta.validation;
    requires jakarta.ws.rs;
    requires jersey.container.servlet.core;
    requires jersey.common;
    requires jersey.hk2;
    requires jersey.server;
    requires jul.to.slf4j;
    requires net.bytebuddy;
    requires org.apache.commons.lang3;
    requires org.apache.commons.codec;
    requires org.apache.commons.pool2;
    requires org.eclipse.jetty.http2.server;
    requires org.eclipse.jetty.rewrite;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.webapp;
    requires org.glassfish.hk2.api;
    requires org.glassfish.hk2.locator;
    requires org.glassfish.hk2.utilities;
    requires org.glassfish.jaxb.core;
    requires org.glassfish.jaxb.runtime;
    requires org.hibernate.validator;
    requires org.postgresql.jdbc;
    requires org.slf4j;
    requires org.yaml.snakeyaml;
    requires spring.aop;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.context.support;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.security.crypto;
    requires spring.security.web;
    requires spring.web;
    requires spring.webmvc;
    requires thymeleaf;
    requires thymeleaf.spring6;
    requires thymeleaf.extras.springsecurity6;

    exports org.randomcoder.website.data;
    exports org.randomcoder.website.model;

    opens database;

    opens org.randomcoder.website;
    opens org.randomcoder.website.bo;
    opens org.randomcoder.website.contentfilter;
    opens org.randomcoder.website.controller;
    opens org.randomcoder.website.command;
    opens org.randomcoder.website.dao;
    opens org.randomcoder.website.data;
    opens org.randomcoder.website.feed;
    opens org.randomcoder.website.model;
    opens org.randomcoder.website.validation;
    opens org.randomcoder.website.jaxrs.features;
    opens org.randomcoder.website.jaxrs.providers;
    opens org.randomcoder.website.jaxrs.resources;

    // static content
    opens org.randomcoder.website.content;
    opens org.randomcoder.website.content.css;
    opens org.randomcoder.website.content.js;
    opens org.randomcoder.website.content.js.lib;
    opens org.randomcoder.website.content.images;
    opens org.randomcoder.website.content.images.badges;
    opens org.randomcoder.website.content.images.silk;

    // thymeleaf templates
    opens org.randomcoder.website.templates;
    opens org.randomcoder.website.templates.content;
    opens org.randomcoder.website.templates.footer;
    opens org.randomcoder.website.templates.head;
    opens org.randomcoder.website.templates.header;
    opens org.randomcoder.website.templates.sidebar;

    opens profiles;

}

