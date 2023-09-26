/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  collection.createRule = "@collection.scholarship_creators.user_id ?= @request.auth.id && @request.data.owner = @request.auth.id &&\n@request.data.match_criteria = \"{}\""

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  collection.createRule = "@collection.scholarship_creators.user_id ?= @request.auth.id && @request.data.owner = @request.auth.id"

  return dao.saveCollection(collection)
})
