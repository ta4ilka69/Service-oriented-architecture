import { endpoints } from '../api/catalog'
import type { Endpoint } from '../api/catalog'
import List from '@mui/material/List'
import ListItem from '@mui/material/ListItem'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import Divider from '@mui/material/Divider'
import MusicNoteIcon from '@mui/icons-material/MusicNote'
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents'

type Props = {
   selectedId?: string
   onSelect: (e: Endpoint) => void
}

export function EndpointList({ selectedId, onSelect }: Props) {
   return (
      <List dense sx={{ width: '100%', py: 0 }}>
         {endpoints.map((e) => (
            <ListItem key={e.id} disablePadding>
               <ListItemButton selected={e.id === selectedId} onClick={() => onSelect(e)} sx={{ py: 1 }}>
                  <ListItemIcon sx={{ minWidth: 32, color: 'primary.main' }}>
                     {e.service === 'music' ? <MusicNoteIcon /> : <EmojiEventsIcon />}
                  </ListItemIcon>
                  <ListItemText
                     primary={e.title}
                     secondary={`${e.method} ${e.path}`}
                     primaryTypographyProps={{ fontWeight: 600 }}
                     secondaryTypographyProps={{ color: 'text.secondary' }}
                  />
               </ListItemButton>
            </ListItem>
         ))}
         <Divider />
      </List>
   )
}

export default EndpointList


