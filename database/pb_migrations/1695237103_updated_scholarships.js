/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "wwbuuxvs",
    "name": "match_criteria",
    "type": "json",
    "required": false,
    "presentable": false,
    "unique": false,
    "options": {}
  }))

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  // remove
  collection.schema.removeField("wwbuuxvs")

  return dao.saveCollection(collection)
})
