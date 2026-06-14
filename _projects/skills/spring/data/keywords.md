---
layout: default
title:
---

[fundamentals](#fundamentals)

[mapping annotations](#mapping-annotations)

[repository](#repository)

[query, tuning](#query-tuning)

[advanced](#advanced)


## fundamentals

### jpa

### spring data

### hibernate

### entity

@Entity, @Id

default constructor

non-final, non-static

equals(), hashCode() override by entity id

### persistence unit

EntityManagerFactory

singleton

multi-tenant - @Primary, @Qualifier

### persistence context

EntityManager

per request

persistence management

entity crud operation

entity lifecycle: transient, persistent, detached, removed

1st level cache, dirty checking, write-behind, lazy loading

+@Transactional

### transaction management

acid

propagation

local(RESOURCE_LOCAL) - EntityManager

jta(java transaction api)

@Transactional

@Async

rollback


## mapping annotations

### @Entity, @Table

### @Id, @GeneratedValue

### @Column

### @Enumerated

### @OneToOne, @OneToMany, @ManyToOne, @ManyToMany

### @JoinColumn, @JoinTable

### @Embedded, @Embeddable

### @MappedSuperclass

### @Inheritance


## repository

### Repository, CrudRepository, PagingAndSortingRepository

### JpaRepository

### @Query

### query method

### specifications

### projections


## transaction, cache

### @Transactional

### propagation

### @Lock (pessimistic/optimistic)

### 1st cache (persistence context)

### 2nd cache (redis ...)


## query, tuning

### named query

### @Query

### criteria api

### querydsl

### entity graph

### n+1 problem

### lazy loading vs eager loading

### fetch join

### batch fetching

### @Modifying


## advanced

### auditing (@CreatedDate, @LastModifiedDate)

### soft delete (@Where, @SQLDelete)

### custom repository implementation

### event listener (@EntityListeners, @PrePersist, @PostLoad)