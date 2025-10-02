import type { Endpoint, Param } from '../api/catalog'
import Box from '@mui/material/Box'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import MenuItem from '@mui/material/MenuItem'
import { useMemo } from 'react'


type Props = {
   endpoint: Endpoint
   values: Record<string, string>
   onChange: (name: string, value: string) => void
}

function Field({ p, value, onChange }: { p: Param; value: string; onChange: (v: string) => void }) {
   const label = useMemo(() => {
      const req = p.required ? ' (обяз.)' : ''
      const type = p.type
      const range = p.min != null ? `, min: ${p.min}` : ''
      return `${p.name}${req} — ${type}${range}`
   }, [p])

   if (p.enum && p.enum.length) {
      return (
         <TextField select fullWidth label={label} value={value ?? ''} onChange={(e) => onChange(e.target.value)} helperText={p.description || `Пример: ${p.example ?? ''}`}>
            {p.enum.map((opt) => (
               <MenuItem key={opt} value={opt}>
                  {opt}
               </MenuItem>
            ))}
         </TextField>
      )
   }

   const multiline = p.in === 'body'
   return (
      <TextField
         fullWidth
         multiline={multiline}
         minRows={multiline ? 6 : undefined}
         label={label}
         value={value ?? ''}
         onChange={(e) => onChange(e.target.value)}
         placeholder={p.example}
         helperText={p.description || (p.example ? `Пример: ${p.example}` : undefined)}
      />
   )
}

export function RequestForm({ endpoint, values, onChange }: Props) {
   return (
      <Box sx={{ p: 2, height: '100%', display: 'flex', flexDirection: 'column' }}>
         <Typography variant="h6" sx={{ mb: 2, color: 'primary.main', fontWeight: 700 }}>
            {endpoint.method} {endpoint.path}
         </Typography>
         <Box
            sx={{
               display: 'grid',
               gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
               gap: 2,
               alignItems: 'start',
               width: '100%',
               flex: 1,
            }}
         >
            {endpoint.params.map((p) => (
               <Box key={p.name} sx={{ gridColumn: { xs: 'auto', md: p.in === 'body' ? '1 / -1' : 'auto' } }}>
                  <Field p={p} value={values[p.name] || ''} onChange={(v) => onChange(p.name, v)} />
               </Box>
            ))}
         </Box>
      </Box>
   )
}

export default RequestForm


