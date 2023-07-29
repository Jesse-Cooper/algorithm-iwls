# Iteratively reWeighted Least Squares (IWLS)

Iteratively reWeighted Least Squares (IWLS) is a method of finding the Maximum Likelihood Estimates (MLE) for a General Linear Model (GLM). This project implements GLMs for both Binomial and Poisson distributions.

## Application

A Java package implementing GLMs for Binomial and Poisson distribution is provided. An example program is also provide that fits the same data to both a Binomial and Poisson distribution.

This project works with `Java 17`.

### Package Usage

Only full rank models are supported.

Models can handle numerical, factors and interaction of explanatory variables.

* `A` = discrete.
* `B` = continuous.
* `C` = Three level factor (1, 2, 3) with `C = 1` as a base factor.

The explanatory matrix below shows `A`, `B` and `C` with interaction with `A` on `B` and `C`.

```java
//      A,   B, C=2, C=3, A*B, A*(C=2), A*(C=3)
xs = {{10, 1.0,   0,   0,  10,       0,      0},
      {20, 1.1,   0,   0,  22,       0,      0},
      {30, 1.2,   1,   0,  36,      30,      0},
      {40, 1.3,   1,   0,  52,      40,      0},
      {50, 1.4,   0,   1,  70,       0,     50},
      {60, 1.5,   0,   1,  90,       0,     60},
      {70, 1.6,   0,   1, 112,       0,     70}}
```

### Example Program

An artificial dataset scenario for the effect of fertiliser strength on plant survival shown below was created to fit both example models.

| Strength  |  1 |  2 |  3 | Total |
|:----------|:--:|:--:|:--:|:-----:|
| Survivors | 32 | 25 | 10 |    67 |
| Deaths    |  6 | 17 | 10 |    33 |
| Total     | 38 | 42 | 20 |   100 |
