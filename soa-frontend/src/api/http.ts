import { XMLParser } from 'fast-xml-parser'
import fetch from 'cross-fetch'

export type HttpResult = {
  ok: boolean
  status: number
  data?: any
  error?: { code?: number; message?: string; raw?: string }
  isList?: boolean
}

const parser = new XMLParser({
  ignoreAttributes: false,
  attributeNamePrefix: '',
  parseTagValue: true,
  trimValues: true,
})

export async function requestXML(method: string, url: string, bodyXml?: string): Promise<HttpResult> {
  const MUSIC_BASE  = (import.meta as any).env?.VITE_MUSIC_API_HTTPS  || 'https://localhost:5252'
  const GRAMMY_BASE = (import.meta as any).env?.VITE_GRAMMY_API_HTTPS  || 'https://localhost:5317'

  const resolveHttpsUrl = (input: string): string => {
    const base = input.startsWith('/music-bands')
      || input.startsWith('/music')
      ? MUSIC_BASE
      : GRAMMY_BASE
    return new URL(input, base).toString()
  }

  const targetUrl = resolveHttpsUrl(url)
  const headers: Record<string, string> = {
    'Accept': 'application/xml'
  }
  if (bodyXml) headers['Content-Type'] = 'application/xml'

  try {
    const res = await fetch(targetUrl, {
      method,
      headers,
      body: bodyXml,
      mode: 'cors',
      credentials: 'omit',
      redirect: 'follow',
    } as RequestInit)

    const text = await res.text()

    if (!text) {
      return { ok: res.ok, status: res.status }
    }

    let parsed: any
    try {
      parsed = parser.parse(text)
    } catch {
      return { ok: res.ok, status: res.status, error: { raw: text } }
    }

    if (res.ok) {
      const isList = !!(parsed?.musicBands || parsed?.items)
      return { ok: true, status: res.status, data: parsed, isList }
    } else {
      const err = parsed?.error
      if (err && typeof err === 'object') {
        return { ok: false, status: res.status, error: { code: Number(err.code), message: String(err.message) }, data: parsed }
      }
      return { ok: false, status: res.status, error: { raw: text }, data: parsed }
    }
  } catch (e: any) {
    return { ok: false, status: 0, error: { message: e?.message || 'Network error' } }
  }
}


