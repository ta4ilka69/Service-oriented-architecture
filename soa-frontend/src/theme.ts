import { createTheme } from '@mui/material/styles'

// Тема: чёрный и тёмно-жёлтый, острые углы
export const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#C9A227', // тёмно-жёлтый
      contrastText: '#000000',
    },
    secondary: {
      main: '#000000',
    },
    background: {
      default: '#0a0a0a',
      paper: '#121212',
    },
    text: {
      primary: '#ffffff',
      secondary: '#d1c48a',
    },
  },
  shape: {
    borderRadius: 0,
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          border: '1px solid #C9A227',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 0,
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        variant: 'outlined',
        size: 'small',
      },
    },
  },
})


