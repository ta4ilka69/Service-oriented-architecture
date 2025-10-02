import Box from '@mui/material/Box'
import Button from '@mui/material/Button'

type Props = {
   onSend: () => void
}

export function SplitterWithSend({ onSend }: Props) {
   return (
      <Box
         sx={{
            position: 'relative',
            height: 12,
            backgroundColor: 'background.paper',
            borderTop: '1px solid',
            borderBottom: '1px solid',
            borderColor: 'primary.main',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
         }}
      >
         <Button
            variant="contained"
            color="primary"
            onClick={onSend}
            sx={{
               minWidth: 140,
               transform: 'translateY(-50%)',
               position: 'absolute',
               top: '50%',
               borderRadius: 0,
               fontWeight: 800,
            }}
         >
            Отправить
         </Button>
      </Box>
   )
}

export default SplitterWithSend


