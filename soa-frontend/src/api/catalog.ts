export type Param = {
  name: string
  in: 'path' | 'query' | 'body'
  type: 'string' | 'int32' | 'int64' | 'float' | 'xml'
  required?: boolean
  description?: string
  enum?: string[]
  min?: number
  example?: string
  // If true, this query param can be provided multiple times (e.g. sort=..&sort=..)
  multiple?: boolean
}

export type Endpoint = {
  id: string
  service: 'music' | 'grammy'
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
  path: string
  title: string
  description?: string
  params: Param[]
  responseType: 'xml-object' | 'xml-list' | 'status-only'
}

export const endpoints: Endpoint[] = [
  // MUSIC SERVICE
  {
    id: 'music-list',
    service: 'music',
    method: 'GET',
    path: '/music-bands',
    title: 'List MusicBands',
    description: 'Список групп с фильтрацией, сортировкой и пагинацией',
    params: [
      { name: 'sort', in: 'query', type: 'string', description: "Поля сортировки. Префикс '-' — по убыванию", example: '-name', multiple: true },
      { name: 'page', in: 'query', type: 'int32', description: '>= 1', min: 1, example: '1' },
      { name: 'size', in: 'query', type: 'int32', description: '>= 1', min: 1, example: '10' },
      { name: 'filter', in: 'query', type: 'string', description: 'field(>=|<=|>|<|!=|==|^=|$=|@=)value', example: 'numberOfParticipants<100', multiple: true },
    ],
    responseType: 'xml-list',
  },
  {
    id: 'music-create',
    service: 'music',
    method: 'POST',
    path: '/music-bands',
    title: 'Create MusicBand',
    params: [
      { name: 'body', in: 'body', type: 'xml', required: true, description: 'MusicBand (XML)', example: '<musicBand>...</musicBand>' },
    ],
    responseType: 'xml-object',
  },
  {
    id: 'music-get',
    service: 'music',
    method: 'GET',
    path: '/music-bands/{id}',
    title: 'Get MusicBand by ID',
    params: [
      { name: 'id', in: 'path', type: 'int32', required: true, description: '>= 1', min: 1, example: '1' },
    ],
    responseType: 'xml-object',
  },
  {
    id: 'music-put',
    service: 'music',
    method: 'PUT',
    path: '/music-bands/{id}',
    title: 'Replace MusicBand',
    params: [
      { name: 'id', in: 'path', type: 'int32', required: true, min: 1, example: '1' },
      { name: 'body', in: 'body', type: 'xml', required: true, description: 'MusicBand (XML)', example: '<musicBand>...</musicBand>' },
    ],
    responseType: 'xml-object',
  },
  {
    id: 'music-delete',
    service: 'music',
    method: 'DELETE',
    path: '/music-bands/{id}',
    title: 'Delete MusicBand',
    params: [
      { name: 'id', in: 'path', type: 'int32', required: true, min: 1, example: '1' },
    ],
    responseType: 'status-only',
  },
  {
    id: 'music-patch',
    service: 'music',
    method: 'PATCH',
    path: '/music-bands/{id}',
    title: 'Patch MusicBand',
    params: [
      { name: 'id', in: 'path', type: 'int32', required: true, min: 1, example: '1' },
      { name: 'body', in: 'body', type: 'xml', required: true, description: 'MusicBandPatch (XML)', example: '<musicBand>...</musicBand>' },
    ],
    responseType: 'xml-object',
  },
  {
    id: 'music-delete-all-desc',
    service: 'music',
    method: 'DELETE',
    path: '/music-bands/all-with-description',
    title: 'Delete All With Description',
    params: [
      { name: 'description', in: 'query', type: 'string', required: true, example: 'test' },
    ],
    responseType: 'status-only',
  },
  {
    id: 'music-delete-one-genre',
    service: 'music',
    method: 'DELETE',
    path: '/music-bands/one-with-genre',
    title: 'Delete One With Genre',
    params: [
      { name: 'genre', in: 'query', type: 'string', required: true, enum: ['PSYCHEDELIC_CLOUD_RAP','SOUL','POP'], example: 'POP' },
    ],
    responseType: 'status-only',
  },
  {
    id: 'music-count-album',
    service: 'music',
    method: 'GET',
    path: '/music-bands/count-best-album',
    title: 'Count Best Album >',
    params: [
      { name: 'albumName', in: 'query', type: 'string', required: true, example: 'Greatest' },
      { name: 'albumTracks', in: 'query', type: 'int64', required: true, min: 1, example: '10' },
    ],
    responseType: 'xml-object',
  },

  // GRAMMY SERVICE
  {
    id: 'grammy-add-single',
    service: 'grammy',
    method: 'POST',
    path: '/api/v1/grammy/band/{band-id}/singles/add',
    title: 'Add Single to Band',
    params: [
      { name: 'band-id', in: 'path', type: 'int32', required: true, min: 1, example: '1' },
      { name: 'body', in: 'body', type: 'xml', required: true, description: 'SingleSchema (XML)', example: '<singleSchema><name>Hit</name></singleSchema>' },
    ],
    responseType: 'xml-object',
  },
  {
    id: 'grammy-add-participant',
    service: 'grammy',
    method: 'POST',
    path: '/api/v1/grammy/band/{band-id}/participants/add',
    title: 'Add Participant to Band',
    params: [
      { name: 'band-id', in: 'path', type: 'int32', required: true, min: 1, example: '1' },
      { name: 'body', in: 'body', type: 'xml', required: true, description: 'ParticipantSchema (XML)', example: '<participantSchema><name>John</name></participantSchema>' },
    ],
    responseType: 'xml-object',
  },
]


