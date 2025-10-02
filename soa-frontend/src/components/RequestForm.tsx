import type { Endpoint, Param } from '../api/catalog'
import Box from '@mui/material/Box'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import MenuItem from '@mui/material/MenuItem'
import { useMemo } from 'react'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline'


type Props = {
   endpoint: Endpoint
   values: Record<string, string | string[]>
   onChange: (name: string, value: string | string[]) => void
}

function Field({ p, value, onChange }: { p: Param; value: string | string[]; onChange: (v: string | string[]) => void }) {
   const label = useMemo(() => {
      const req = p.required ? ' (обяз.)' : ''
      const type = p.type
      const range = p.min != null ? `, min: ${p.min}` : ''
      return `${p.name}${req} — ${type}${range}`
   }, [p])

   // Multiple values for query params like sort/filter
   if (p.in === 'query' && p.multiple) {
      const items = Array.isArray(value) ? value : (value ? [value] : [''])
      return (
         <Box>
            {items.map((val, idx) => (
               <Box key={idx} sx={{ display: 'flex', gap: 1, alignItems: 'center', mb: 1 }}>
                  <TextField
                     fullWidth
                     label={idx === 0 ? label : `${p.name}`}
                     value={val ?? ''}
                     onChange={(e) => {
                        const next = [...items]
                        next[idx] = e.target.value
                        onChange(next)
                     }}
                     placeholder={p.example}
                     helperText={idx === 0 ? (p.description || (p.example ? `Пример: ${p.example}` : undefined)) : undefined}
                  />
                  {items.length > 1 && (
                     <IconButton aria-label="remove" onClick={() => {
                        const next = items.filter((_, i) => i !== idx)
                        onChange(next.length ? next : [''])
                     }}>
                        <DeleteOutlineIcon />
                     </IconButton>
                  )}
               </Box>
            ))}
            <Button variant="outlined" onClick={() => onChange([...(Array.isArray(value) ? value : (value ? [value] : [''])), ''])}>Добавить строку</Button>
         </Box>
      )
   }

   if (p.enum && p.enum.length) {
      return (
         <TextField select fullWidth label={label} value={(value as string) ?? ''} onChange={(e) => onChange(e.target.value)} helperText={p.description || `Пример: ${p.example ?? ''}`}>
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
         value={(value as string) ?? ''}
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
                  <Field p={p} value={values[p.name] as any} onChange={(v) => onChange(p.name, v)} />
               </Box>
            ))}
         </Box>
      </Box>
   )
}

export default RequestForm


