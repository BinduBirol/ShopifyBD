import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import { RouterLink } from "src/routes/components";

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

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="xs"
            fullWidth
        >
            <DialogTitle>{title}</DialogTitle>

            <DialogContent>
                <DialogContentText>{message}</DialogContentText>
            </DialogContent>

            <DialogActions>

                {
                    link ? (
                        <Button
                            variant="outlined"
                            component={RouterLink}
                            href={link}
                            onClick={onClose}
                            autoFocus
                        >
                            {buttonText ?? t("common.ok")}
                        </Button>
                    ) : (
                        <Button
                            variant="outlined"
                            onClick={onClose}
                            autoFocus
                        >
                            {buttonText ?? t("common.ok")}
                        </Button>
                    )
                }

            </DialogActions>
        </Dialog>
    );
}