import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
} from '@mui/material';

import { useTranslation } from 'react-i18next';

import { RouterLink } from 'src/routes/components';

import { Iconify } from 'src/components/iconify';



type AlertDialogProps = {
    open: boolean;
    title: string;
    message: string;
    buttonText?: string;
    link?: string;
    onClose: () => void;
};


export default function AlertDialog({
    open,
    title,
    message,
    buttonText,
    link,
    onClose,
}: AlertDialogProps) {

    const { t } = useTranslation();


    const buttonProps = link
        ? {
            component: RouterLink,
            href: link,
        }
        : {};


    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="xs"
            fullWidth
        >

            <DialogTitle>
                {title}
            </DialogTitle>


            <DialogContent>
                <DialogContentText>
                    {message}
                </DialogContentText>
            </DialogContent>


            <DialogActions sx={{ px: 3, pb: 2 }}>

                <Button
                    variant="contained"
                    onClick={onClose}
                    autoFocus
                    {...buttonProps}
                    endIcon={
                        link ? (
                            <Iconify                                
                                icon="eva:arrow-ios-forward-fill"
                            />
                        ) : undefined
                    }
                >
                    {buttonText ?? t('common.ok')}
                </Button>

            </DialogActions>

        </Dialog>
    );
}