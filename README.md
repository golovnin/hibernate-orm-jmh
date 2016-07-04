# JMH tests for Hibernate ORM

Currently there are only two JMH tests for Hibernate:

* `EntityKeyBenchmark` - a test for the constructor of EntityKey.

* `ConcurrentServiceBindingBenchmark` - a test for the `#get()`-method of `ConcurrentServiceBinding` including different implementations of `ConcurrentServiceBinding`.

Here is an exaple how to start the tests:

    mvn clean install
    java -jar target/microbenchmarks.jar ConcurrentServiceBindingBenchmark

## License

This software and its documentation are distributed under the terms of the FSF Lesser GNU Public
License (see lgpl.txt).

