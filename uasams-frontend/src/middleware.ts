import { defineMiddleware } from "astro:middleware";

import Pocketbase from "pocketbase";

export const onRequest = defineMiddleware((context, next) => {
    const pb = new Pocketbase("http://127.0.0.1:7001");

    context.locals.pb = pb;

    return next();
});