package main

import (
	"log"
	"net/http"

	"github.com/neelance/graphql-go"
	"github.com/neelance/graphql-go/relay"
)

var Schema = `
schema { query: Query }

type Query{
  Integers: [Int]!
}
`

var schema *graphql.Schema

type Resolver struct{}

func (r *Resolver) Integers() []*int32 {
	var a, b, c int32
	a = 1
	b = 2
	c = 3
	return []*int32{&a, &b, &c}
}

func init() {
	var err error
	schema, err = graphql.ParseSchema(Schema, &Resolver{})
	if err != nil {
		panic(err)
	}
}

func main() {
	http.Handle("/query", &relay.Handler{Schema: schema})
	http.Handle("/graphql", &relay.Handler)
	log.Fatal(http.ListenAndServe(":8080", nil))

}
