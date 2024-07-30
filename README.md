# Iteratively reWeighted Least Squares (IWLS)

Iteratively reWeighted Least Squares (IWLS) is a method of finding the Maximum
Likelihood Estimates (MLEs) for a General Linear Model (GLM).

## Application

A Java package implementing GLMs for Binomial, Gaussian and Poisson
distributions is provided.

An example program is also provided that fits the same data in different formats
to both a Binomial and Poisson distribution.

## Package Usage

Explanatory matrices must not have fewer instances (rows) than features
(columns); otherwise, the model will be oversaturated.

Models can be either full rank and less than full rank and can handle
numerical variables, factors and interactions.

* `A` = Discrete
* `B` = Continuous
* `C` = Three level factor (1, 2, 3) with `C = 1` as a base factor

The explanatory matrix below shows the main effects `A`, `B` and `C` with `A`
having two-way interactions on both `B` and `C`.

```java
//   intercept,  A,   B, C=2, C=3, A*B, A*(C=2), A*(C=3)
xs = {
    {        1, 45, 1.4,   1,   0,  63,      45,       0},
    {        1, 12, 1.5,   0,   1,  18,       0,      12},
    {        1, 50, 1.6,   0,   0,  80,       0,       0},
    {        1, 80, 1.1,   1,   0,  88,      80,       0},
    {        1,  5, 1.8,   0,   1,   9,       0,       5},
    {        1, 50, 1.3,   0,   1,  65,       0,      50},
    {        1, 15, 1.2,   0,   0,  18,       0,       0},
    {        1, 55, 1.4,   0,   1,  77,       0,      55},
    {        1, 12, 1.5,   1,   0,  18,      12,       0},
    {        1, 60, 1.3,   0,   0,  78,       0,       0}
};
```

## Example Program

An artificial scenario for the effect of fertiliser strength on plant survival,
as shown below, was created for the example models.

| Strength  |  1 |  2 |  3 | Total |
|:----------|:--:|:--:|:--:|:-----:|
| Survivors | 32 | 25 | 10 |    67 |
| Deaths    |  6 | 17 | 10 |    33 |
| Total     | 38 | 42 | 20 |   100 |

## Invoking Instructions

A Makefile is provided that compiles the example program.

To compile use `make` then run with `java Example`.

This project was implemented in `Java 21`.
