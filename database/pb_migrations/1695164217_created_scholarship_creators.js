/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const collection = new Collection({
    "id": "prpo42cuho8tmsf",
    "created": "2023-09-19 22:56:57.512Z",
    "updated": "2023-09-19 22:56:57.512Z",
    "name": "scholarship_creators",
    "type": "base",
    "system": false,
    "schema": [
      {
        "system": false,
        "id": "0oocrhjk",
        "name": "user_id",
        "type": "relation",
        "required": false,
        "presentable": false,
        "unique": false,
        "options": {
          "collectionId": "_pb_users_auth_",
          "cascadeDelete": false,
          "minSelect": null,
          "maxSelect": 1,
          "displayFields": null
        }
      }
    ],
    "indexes": [],
    "listRule": null,
    "viewRule": null,
    "createRule": null,
    "updateRule": null,
    "deleteRule": null,
    "options": {}
  });

  return Dao(db).saveCollection(collection);
}, (db) => {
  const dao = new Dao(db);
  const collection = dao.findCollectionByNameOrId("prpo42cuho8tmsf");

  return dao.deleteCollection(collection);
})
