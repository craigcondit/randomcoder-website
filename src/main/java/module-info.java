module org.randomcoder.website {
    requires java.base;
    requires java.desktop;
    requires java.sql;
    requires jdk.unsupported;

    requires ch.qos.logback.classic;
    requires commons.httpclient;
    requires com.fasterxml.classmate;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.module.jakarta.xmlbind;
    requires jakarta.el;
    requires jakarta.inject;
    requires jakarta.xml.bind;
    requires jersey.container.servlet.core;
    requires jersey.common;
    requires jersey.hk2;
    requires jersey.server;
    requires jul.to.slf4j;
    requires net.bytebuddy;
    requires org.apache.commons.lang3;
    requires org.apache.commons.codec;
    requires org.eclipse.jetty.http2.server;
    requires org.eclipse.jetty.rewrite;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.webapp;
    requires org.glassfish.hk2.api;
    requires org.glassfish.hk2.utilities;
    requires org.glassfish.jaxb.core;
    requires org.glassfish.jaxb.runtime;
    requires org.hibernate.validator;
    requires org.postgresql.jdbc;
    requires org.slf4j;
    requires org.yaml.snakeyaml;
    requires thymeleaf;
    requires thymeleaf.spring6;
    requires thymeleaf.extras.springsecurity6;
    requires spring.aop;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.context.support;
    requires spring.data.commons;
    requires spring.jdbc;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.security.crypto;
    requires spring.security.web;
    requires spring.web;
    requires spring.webmvc;
    requires jakarta.ws.rs;

    exports org.randomcoder.providers;
    exports org.randomcoder.resources;

    opens database;
    opens org.randomcoder.article;
    opens org.randomcoder.article.comment;
    opens org.randomcoder.article.moderation;
    opens org.randomcoder.bo;
    opens org.randomcoder.config;
    opens org.randomcoder.content;
    opens org.randomcoder.dao;
    opens org.randomcoder.db;
    opens org.randomcoder.feed;
    opens org.randomcoder.func;
    opens org.randomcoder.io;
    opens org.randomcoder.mvc.command;
    opens org.randomcoder.mvc.controller;
    opens org.randomcoder.mvc.editor;
    opens org.randomcoder.mvc.validator;
    opens org.randomcoder.pagination;
    opens org.randomcoder.security;
    opens org.randomcoder.security.spring;
    opens org.randomcoder.staticcontent;
    opens org.randomcoder.staticcontent.css;
    opens org.randomcoder.staticcontent.js;
    opens org.randomcoder.staticcontent.js.lib;
    opens org.randomcoder.staticcontent.images;
    opens org.randomcoder.staticcontent.images.badges;
    opens org.randomcoder.staticcontent.images.silk;
    opens org.randomcoder.templates;
    opens org.randomcoder.templates.content;
    opens org.randomcoder.templates.footer;
    opens org.randomcoder.templates.head;
    opens org.randomcoder.templates.header;
    opens org.randomcoder.templates.sidebar;
    opens org.randomcoder.tag;
    opens org.randomcoder.user;
    opens org.randomcoder.validation;
    opens org.randomcoder.xml;
    opens profiles;
    opens webapp;
    opens webapp.css;
    opens webapp.images;
    opens webapp.images.silk;
    opens webapp.images.badges;
    opens webapp.js;
    opens webapp.js.lib;
}

