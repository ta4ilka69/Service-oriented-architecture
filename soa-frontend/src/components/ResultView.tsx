import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'

type Props = {
   ok: boolean
   status: number
   data?: any
   error?: { code?: number; message?: string; raw?: string }
}

function toRows(list: any[]): { [k: string]: any }[] {
   if (!Array.isArray(list)) return []
   return list.map((it: any) => (typeof it === 'object' ? it : { value: it }))
}

export function ResultView({ ok, status, data, error }: Props) {
   if (!ok) {
      return (
         <Paper sx={{ p: 2, borderColor: '#ff5252' }}>
            <Typography variant="subtitle1" sx={{ color: '#ff5252', fontWeight: 700 }}>
               Ошибка {status || ''}
            </Typography>
            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
               {error?.message || error?.raw || 'Unknown error'}
            </Typography>
         </Paper>
      )
   }


   if (!data) {
      return (
         <Paper sx={{ p: 2 }}>
            <Typography variant="body2">Нет содержимого. Статус {status}.</Typography>
         </Paper>
      )
   }

   const list =
      data?.musicBands?.musicBandAllSchema ||
      data?.musicBands?.items ||
      data?.items ||
      []

   if (Array.isArray(list)) {
      const rows = toRows(list)
      const columnsSet = new Set<string>()
      rows.forEach((row) => {
         Object.keys(row || {}).forEach((k) => columnsSet.add(k))
      })
      const columns = Array.from(columnsSet)

      return (
         <Paper sx={{ p: 0, height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ px: 2, py: 1, borderBottom: '1px solid', borderColor: 'primary.main' }}>
               <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>Результаты (строк: {rows.length})</Typography>
            </Box>
            <Box sx={{ overflow: 'auto', flex: 1 }}>
               <Table size="small" stickyHeader>
                  <TableHead>
                     <TableRow>
                        {columns.map((c) => (
                           <TableCell key={c} sx={{ borderColor: 'divider' }}>
                              {c}
                           </TableCell>
                        ))}
                     </TableRow>
                  </TableHead>
                  <TableBody>
                     {rows.map((r, idx) => (
                        <TableRow key={idx}>
                           {columns.map((c) => (
                              <TableCell key={c} sx={{ borderColor: 'divider', whiteSpace: 'nowrap', maxWidth: 320, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                                 {typeof r?.[c] === 'object' ? JSON.stringify(r?.[c]) : String(r?.[c] ?? '')}
                              </TableCell>
                           ))}
                        </TableRow>
                     ))}
                  </TableBody>
               </Table>
            </Box>
         </Paper>
      )
   }

   return (
      <Paper sx={{ p: 2 }}>
         <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1 }}>Ответ</Typography>
         <pre style={{ margin: 0, whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>{JSON.stringify(data, null, 2)}</pre>
      </Paper>
   )
}

export default ResultView


