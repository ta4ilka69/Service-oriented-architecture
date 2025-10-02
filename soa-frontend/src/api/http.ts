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
  const HTTPS_BASE = (import.meta as any).env?.VITE_API_BASE_HTTPS || 'https://localhost:5252'

  const resolveHttpsUrl = (input: string): string => {
    try {
      if (input.startsWith('/')) {
        return new URL(input, HTTPS_BASE).toString()
      }
      const u = new URL(input, HTTPS_BASE)
      if (u.protocol !== 'https:') {
        u.protocol = 'https:'
      }
      return u.toString()
    } catch {
      return new URL(input, HTTPS_BASE).toString()
    }
  }

  const targetUrl = resolveHttpsUrl(url)
  const headers: Record<string, string> = { 'Accept': 'application/xml' }
  if (bodyXml) headers['Content-Type'] = 'application/xml'

  try {
    const res = await fetch(targetUrl, {
      method,
      headers,
      body: bodyXml,
    })

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


