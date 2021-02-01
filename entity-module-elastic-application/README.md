# Application to test support for elasticsearch

## Run docker

~~~
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.9.3
~~~

Delete your index:

~~~
curl -XDELETE localhost:9200/countrycustomeridx
~~~

View index mapping:

~~~
curl -X GET "localhost:9200/countrycustomeridx/_mapping?pretty"
~~~

View your index:

~~~
curl -X GET "localhost:9200/countrycustomeridx/_search?pretty" -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_all": {}
    }
}
'
~~~