# Hacker News challenge

La aplicación obtiene mediante scraping las primeras 30 stories de Hacker News,
aplica uno de los filtros del challenge y registra cada operación en H2.

## API

```text
GET /api/stories?filter=MORE_THAN_FIVE_WORDS
GET /api/stories?filter=FIVE_OR_FEWER_WORDS
```

Los filtros se ordenan por comentarios y puntos, respectivamente. Un filtro
desconocido devuelve `400 Bad Request`.

## Usage data

Cada operación guarda en la tabla `usage_data`:

- `id`
- `request_timestamp`
- `applied_filter`
- `result_count`
- `duration_ms`

La base H2 se configura en memoria para desarrollo y pruebas.
