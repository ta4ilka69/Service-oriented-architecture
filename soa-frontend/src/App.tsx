import { useMemo, useState } from 'react'
import Box from '@mui/material/Box'
import CssBaseline from '@mui/material/CssBaseline'
import { ThemeProvider } from '@mui/material/styles'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import Divider from '@mui/material/Divider'
import { theme } from './theme'
import EndpointList from './components/EndpointList'
import { endpoints } from './api/catalog'
import type { Endpoint, Param } from './api/catalog'
import RequestForm from './components/RequestForm'
import ResultView from './components/ResultView'
import SplitterWithSend from './components/SplitterWithSend'
import { requestXML } from './api/http'

function App() {
  const [selected, setSelected] = useState<Endpoint>(endpoints[0])
  const [values, setValues] = useState<Record<string, string | string[]>>({})
  const [result, setResult] = useState<{ ok: boolean; status: number; data?: any; error?: any }>({ ok: true, status: 0 })

  const formValues = useMemo(() => {
    const init: Record<string, string | string[]> = {}
    selected.params.forEach((p) => {
      if (values[p.name] == null && p.example != null) {
        init[p.name] = p.multiple ? [String(p.example)] : String(p.example)
      }
    })
    return { ...init, ...values }
  }, [selected, values])

  const handleChange = (name: string, value: string | string[]) => {
    setValues((v) => ({ ...v, [name]: value }))
  }

  const buildUrl = (): string => {
    let url = selected.path
    // path params
    selected.params.filter(p => p.in === 'path').forEach(p => {
      url = url.replace(`{${p.name}}`, encodeURIComponent(formValues[p.name] || ''))
    })
    // query params (support multiple values)
    const encodePair = (p: Param, v: string) => `${encodeURIComponent(p.name)}=${encodeURIComponent(v)}`
    const pairs: string[] = []
    selected.params.filter(p => p.in === 'query').forEach(p => {
      const val = formValues[p.name]
      if (val == null) return
      if (Array.isArray(val)) {
        val.filter(s => s !== '').forEach(v => pairs.push(encodePair(p, v)))
      } else if (val !== '') {
        pairs.push(encodePair(p, val))
      }
    })
    const qs = pairs.join('&')
    if (qs) url += (url.includes('?') ? '&' : '?') + qs
    return url
  }

  const onSend = async () => {
    const url = buildUrl()
    const bodyXml = selected.params.find(p => p.in === 'body') ? (formValues['body'] || '') : undefined
    const res = await requestXML(selected.method, url, bodyXml)
    setResult(res)
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ height: '100vh', width: '100vw', display: 'flex', flexDirection: 'column', bgcolor: 'background.default' }}>
        {/* Шапка */}
        <Box sx={{ position: 'sticky', top: 0, zIndex: 10, display: 'flex', justifyContent: 'center', alignItems: 'center', py: 1.5, bgcolor: 'background.default', borderBottom: '1px solid', borderColor: 'primary.main' }}>
          <Paper sx={{ px: 3, py: 0.5, borderWidth: 2, borderStyle: 'solid', borderColor: 'secondary.main', bgcolor: 'primary.main' }}>
            <Typography variant="h5" sx={{ fontWeight: 900, color: 'primary.contrastText' }}>
              Music Services
            </Typography>
          </Paper>
        </Box>

        <Divider />

        <Box sx={{ flex: 1, display: 'flex', minHeight: 0 }}>
          {/* Левая панель (10%) */}
          <Box sx={{ width: '10%', minWidth: 220, maxWidth: 320, borderRight: '2px solid', borderColor: 'primary.main', overflow: 'auto' }}>
            <EndpointList selectedId={selected.id} onSelect={(e) => { setSelected(e); setValues({}); setResult({ ok: true, status: 0 }) }} />
          </Box>

          {/* Правая область (оставшееся пространство) */}
          <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
            {/* Верхняя панель: форма */}
            <Box sx={{ flex: 1, minHeight: 0, overflow: 'auto', display: 'flex', justifyContent: 'center', alignItems: 'stretch' }}>
              <Box sx={{ width: '100%' }}>
                <RequestForm endpoint={selected} values={formValues} onChange={handleChange} />
              </Box>
            </Box>

            {/* Разделитель с кнопкой */}
            <SplitterWithSend onSend={onSend} />

            {/* Нижняя панель: результат */}
            <Box sx={{ flex: 1, minHeight: 0, overflow: 'auto', display: 'flex', justifyContent: 'center', alignItems: 'stretch' }}>
              {result.status === 0 ? (
                <Paper sx={{ m: 2, p: 2, width: '100%' }}>
                  <Typography variant="body2" color="text.secondary">Здесь появится результат запроса</Typography>
                </Paper>
              ) : (
                <Box sx={{ m: 2, height: '100%', width: '100%' }}>
                  <ResultView ok={result.ok} status={result.status} data={result.data} error={result.error} />
                </Box>
              )}
            </Box>
          </Box>
        </Box>
      </Box>
    </ThemeProvider>
  )
}

export default App
